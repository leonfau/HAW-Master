package de.haw.trafficcoordination.feeder;

import de.haw.trafficcoordination.common.Entities.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.core.transaction.manager.DistributedJiniTxManagerConfigurer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class CarThread implements Runnable {
    CarImpl car;
    private GigaSpace spa;
    private final static String URL = "jini://*/*/space";
    private final static int MAX_BLOCK = 500000;


    public CarThread(CarImpl car) {
        this.car = car;
        spa = new GigaSpaceConfigurer(new UrlSpaceConfigurer(URL))
                .gigaSpace();
    }

    @Override
    public void run() {
        enterInitialRoxel(this.car.getInitX(), this.car.getInitY());
    }

    private Roxel enterInitialRoxel(int x, int y) {
        Roxel r = null;
        r = takeByCoordinate(x, y);

        return r;
    }


    //---------Space

    public Roxel takeByCoordinate(int x, int y) {
        Roxel query = new Roxel();
        query.setX(x);
        query.setY(y);
        query.setOccupiedBy(new EmptyCar());
        Roxel next = null;
        try {
            PlatformTransactionManager ptm = new DistributedJiniTxManagerConfigurer().transactionManager();
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus ts = ptm.getTransaction(definition);
            try {
                next = spa.takeIfExists(query, MAX_BLOCK);
                if (next == null) {
               //     System.out.println("Init Roxel not found");
//                    throw new RoxelNotFoundException("roxel x: " + query.getX() + " y: " + query.getY());
                    return null;
                }
                next.setOccupiedBy(car);

                Roxel current = car.getRoxel();
                if (current != null) {
                    current.setOccupiedBy(new EmptyCar());
                    spa.write(current);
                }

                System.out.println("Init Roxel: " +x +":" + y);
                car.setRoxel(next);
                spa.write(next);
                spa.write(car, 20);
            } catch (Exception e) {
                ptm.rollback(ts);
                throw e;
            }
            ptm.commit(ts);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return next;
    }
}
