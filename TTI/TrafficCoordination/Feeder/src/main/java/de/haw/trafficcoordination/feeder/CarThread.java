package de.haw.trafficcoordination.feeder;

import com.j_spaces.core.LeaseContext;
import de.haw.trafficcoordination.common.Entities.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.core.transaction.manager.DistributedJiniTxManagerConfigurer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.rmi.dgc.Lease;
import java.util.Date;

public class CarThread implements Runnable {
    CarImpl car;
    private GigaSpace spa;
    private final static String URL = "jini://*/*/space";
    private final static int MAX_BLOCK = 5000;


    public CarThread(CarImpl car) {
        this.car = car;
        spa = new GigaSpaceConfigurer(new UrlSpaceConfigurer(URL))
                .gigaSpace();
    }

    @Override
    public void run() {
        enterInitialRoxel(this.car.getInitX(), this.car.getInitY());
    }


    //---------Space

    public void enterInitialRoxel(int x, int y) {
        Roxel query = new Roxel();
        query.setX(x);
        query.setY(y);
        query.setOccupiedBy(new EmptyCar());
        try {
            PlatformTransactionManager ptm = new DistributedJiniTxManagerConfigurer().transactionManager();
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus ts = ptm.getTransaction(definition);
            try {
                Roxel next = spa.takeIfExists(query);
                if (next == null) {

                    //     System.out.println("Init Roxel not found");
//                    throw new RoxelNotFoundException("roxel x: " + query.getX() + " y: " + query.getY());
                    return;
                }
                next.setOccupiedBy(car);

                Roxel current = car.getRoxel();
                if (current != null) {
                    current.setOccupiedBy(new EmptyCar());
                    spa.write(current);
                }

                System.out.println("Init Roxel: " + x + ":" + y);
                car.setRoxel(next);
                spa.write(next, net.jini.core.lease.Lease.FOREVER);

                LeaseContext<CarImpl> o = spa.write(car, car.getRoxelTimeInMs());
                String UID = o.getUID();
                System.out.println("Current Date:"+ new Date(System.currentTimeMillis()) + " Lease Expiration Date:" + new Date(o.getExpiration()));

            } catch (Exception e) {
                ptm.rollback(ts);
                throw e;
            }
            ptm.commit(ts);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
