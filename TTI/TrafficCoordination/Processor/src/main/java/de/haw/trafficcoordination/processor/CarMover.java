package de.haw.trafficcoordination.processor;

import de.haw.trafficcoordination.common.Entities.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.core.transaction.manager.DistributedJiniTxManagerConfigurer;
import org.openspaces.events.EventDriven;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.logging.Logger;

/**
 * The processor simulates work done no un-processed Data object. The processData
 * accepts a Data object, simulate work by sleeping, and then sets the processed
 * flag to true and returns the processed Data.
 */
@EventDriven
public class CarMover {

    Logger log = Logger.getLogger(this.getClass().getName());
    private final static int MAX_BLOCK = 500000;


    @GigaSpaceContext
    private GigaSpace spa;

    @SpaceDataEvent
    public void moveCarListener(CarImpl car) {
        if (car != null) {
            System.out.println("--------------------");
            System.out.println("--------------------");
            System.out.println("--------------------");
            System.out.println("--------------------");
            this.driveToNextRoxel(car);
        }
    }

    public void driveToNextRoxel(CarImpl car) {
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
                next = spa.takeIfExists(query, MAX_BLOCK);
                if (next == null) {
                    System.out.println("Processor: next not found");
//                    throw new RoxelNotFoundException("roxel not found");
                    return null;
                }
                current.getOccupiedBy();
                car.setRoxel(next);
                next.setOccupiedBy(car);
                current.setOccupiedBy(new EmptyCar());

                System.out.println("Next Roxel: " + next.getX() + ":" + next.getY());
                spa.write(current);
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
