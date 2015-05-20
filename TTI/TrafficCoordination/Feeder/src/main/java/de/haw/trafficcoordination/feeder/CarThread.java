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
    private final int MAX_BLOCK = 500000;


    public CarThread(CarImpl car) {
        this.car = car;
        spa = new GigaSpaceConfigurer(new UrlSpaceConfigurer(URL))
                .gigaSpace();
    }

    @Override
    public void run() {
        Roxel currentRoxel;

        currentRoxel = enterInitialRoxel(this.car.getInitX(), this.car.getInitY());
        if (currentRoxel != null) {
            System.out.println(currentRoxel);
            while (wantToMoveForward()) {
                moveThroughCurrRoxel(currentRoxel);
                currentRoxel = moveToNextRoxel(currentRoxel);
                System.out.println(currentRoxel);
            }
        }
    }

    private Roxel enterInitialRoxel(int x, int y) {
        Roxel r = null;
        r = takeByCoordinate(x, y);
        return r;
    }


    private boolean wantToMoveForward() {
        return true;
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
                next = spa.take(query, MAX_BLOCK);
                if (next == null) {
//                    throw new RoxelNotFoundException("roxel x: " + query.getX() + " y: " + query.getY());
                    return null;
                }
                next.setOccupiedBy(car);
                spa.write(next);

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


    private void moveThroughCurrRoxel(Roxel current) {
        try {
            Thread.sleep(10 * current.getLength());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Roxel moveToNextRoxel(Roxel current) {
        try {
            return takeNextRoxel(current, this.car.getDirection());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Roxel takeNextRoxel(Roxel current, Direction direction) {
        Roxel query = new Roxel();
        query.setX(0);

        int roxelMaxX = spa.count(query);
        query.setX(null);
        query.setY(0);
        int roxelMaxY = spa.count(query);

        switch (direction) {
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
                next = spa.take(query, MAX_BLOCK);
                if (next == null) {
//                    throw new RoxelNotFoundException("roxel not found");
                    return null;
                }
                Car c = current.getOccupiedBy();
                next.setOccupiedBy(c);
                spa.write(next);

                current.setOccupiedBy(new EmptyCar());
                spa.write(current);
                spa.write(next);

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
