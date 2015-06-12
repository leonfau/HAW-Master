package de.haw.trafficcoordination.processor;

import de.haw.trafficcoordination.common.Entities.Direction;
import de.haw.trafficcoordination.common.Entities.Roxel;
import de.haw.trafficcoordination.common.Entities.CarImpl;
import net.jini.core.lease.Lease;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.events.EventDriven;
import org.openspaces.events.adapter.SpaceDataEvent;
import com.j_spaces.core.client.SQLQuery;

import java.util.logging.Logger;

@EventDriven
public class TrafficLight {

    Logger log = Logger.getLogger(this.getClass().getName());

    @GigaSpaceContext
    private GigaSpace spa;


    boolean dir = false;

    @SpaceDataEvent
    public void trafficLightListener(Roxel r) {
    /*    log.info("Traffic Light Event Received");


        Roxel rEast = new Roxel();
        rEast.setOccupiedBy(new CarImpl());
        rEast.setDirection(Direction.EAST);

        int carsEast = spa.count(rEast);

        Roxel rSouth = new Roxel();
        rSouth.setOccupiedBy(new CarImpl());
        rSouth.setDirection(Direction.SOUTH);
        rSouth.setY(r.getY());

        int carsSouth = spa.count(rSouth);
        /*
        int carsEast = spa.readMultiple(
                new SQLQuery<Roxel>(Roxel.class, "occupiedBy = ? AND direction = ? AND x < ?").setParameter(1, new CarImpl()).setParameter(2, Direction.EAST).setParameter(3, r.getX())).length;
        int carsSouth = spa.readMultiple(
                new SQLQuery<Roxel>(Roxel.class, "occupiedBy = ? AND direction = ? AND y < ?").setParameter(1, new CarImpl()).setParameter(2, Direction.SOUTH).setParameter(3, r.getY())).length;
        */
        int carsWest = 0;
        int carsNorth = 0;

        //West

        Roxel query = new Roxel();
        query.setX(r.getX());

        int xSize = spa.count(query);
        int nextX = query.getX() - 1 < xSize ? xSize - query.getX() : query.getX() - 1;

        query.setX(nextX);
        query.setY(r.getY());

        Roxel res = spa.read(query);
        while (res != null && !res.getTrafficLightDirection().equals(Direction.TODECIDE)) {
            if (res.getOccupiedBy() instanceof CarImpl) {
                carsWest++;
            }
            nextX = query.getX() - 1 < xSize ? xSize - query.getX() : query.getX() - 1;
            query.setX(nextX);
            res = spa.read(query);
        }


        query = new Roxel();
        query.setY(r.getY());
        int ySize = spa.count(query);
        query.setX(r.getX());
        int nextY = query.getY() - 1 < ySize ? ySize - query.getY() : query.getY() - 1;

        query.setY(nextY);

        res = spa.read(query);
        while (res != null && !res.getTrafficLightDirection().equals(Direction.TODECIDE)) {
            if (res.getOccupiedBy() instanceof CarImpl) {
                carsNorth++;
            }
            nextY = query.getY() - 1 < ySize ? ySize - query.getY() : query.getY() - 1;
            query.setY(nextY);
            res = spa.read(query);
        }

        if (carsWest > carsNorth) {
            r.setDirection(Direction.EAST);
        } else if (carsWest < carsNorth) {
            r.setDirection(Direction.SOUTH);
        } else {
            if (r.getDirection() == Direction.EAST) {
                r.setDirection(Direction.SOUTH);
            }else {
                r.setDirection(Direction.EAST);
            }
        }
        spa.write(r);

    }
}
