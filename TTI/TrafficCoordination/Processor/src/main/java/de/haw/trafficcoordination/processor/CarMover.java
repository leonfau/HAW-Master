package de.haw.trafficcoordination.processor;

import com.j_spaces.core.LeaseContext;
import de.haw.trafficcoordination.common.Entities.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.core.transaction.manager.DistributedJiniTxManagerConfigurer;
import org.openspaces.events.EventDriven;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.Notify;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.rmi.dgc.Lease;
import java.util.Date;
import java.util.logging.Logger;

@EventDriven
public class CarMover {

    Logger log = Logger.getLogger(this.getClass().getName());

    @GigaSpaceContext
    private GigaSpace spa;

    @SpaceDataEvent
    public void moveCarListener(CarImpl car) {
        log.info(car.getId() + " + Event Received");
        this.takeNextRoxel(car);
    }

    public Roxel takeNextRoxel(CarImpl car) {
        Roxel query = new Roxel();
        query.setX(0);

        int roxelMaxX = spa.count(query);
        query.setX(null);
        query.setY(0);
        int roxelMaxY = spa.count(query);


        Roxel current = car.getRoxel();
        switch (car.getDirection()) {
            case EAST:
                query.setX((current.getX() + 1) % roxelMaxX);
                query.setY(current.getY());
                break;
            case SOUTH:
                query.setX(current.getX());
                query.setY((current.getY() + 1) % roxelMaxY);
                break;
            default:

        }
        query.setOccupiedBy(new EmptyCar());


        Roxel next = null;
        try {
            PlatformTransactionManager ptm = new DistributedJiniTxManagerConfigurer().transactionManager();
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus ts = ptm.getTransaction(definition);
            try {
                next = spa.takeIfExists(query);
                if (next != null) {
                    if (next.getDirection().equals(car.getDirection())) {
                        car.setRoxel(next);
                        next.setOccupiedBy(car);
                        current.setOccupiedBy(new EmptyCar());
                    }
                    spa.write(next, net.jini.core.lease.Lease.FOREVER);
                }
                spa.write(current, net.jini.core.lease.Lease.FOREVER);

                LeaseContext<CarImpl> o = spa.write(car, 100);
                String UID = o.getUID();
                //    System.out.println("Current Date:" + new Date(System.currentTimeMillis()) + " Lease Expiration Date:" + new Date(o.getExpiration()));
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
