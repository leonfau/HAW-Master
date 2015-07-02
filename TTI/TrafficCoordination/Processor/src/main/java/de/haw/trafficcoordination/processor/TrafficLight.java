package de.haw.trafficcoordination.processor;

import de.haw.trafficcoordination.common.Entities.Direction;
import de.haw.trafficcoordination.common.Entities.Roxel;
import de.haw.trafficcoordination.common.Entities.CarImpl;
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

    private static final int RANGE = 5;
    boolean dir = false;

    @SpaceDataEvent
    public void trafficLightListener(Roxel r) {
        log.info("Traffic Light Event Received");

        //North
        SQLQuery<Roxel> query = new SQLQuery<Roxel>(Roxel.class, "x = ? AND y <= ? AND y >= ? AND direction = ? AND occupiedBy = ?");
        int x = r.getX();
        int y = r.getY();
        query.setParameter(1, x);
        query.setParameter(2, y-1 < 0 ? 0 : y-1);
        query.setParameter(3, y-1-RANGE < 0 ? 0 : y-1-RANGE);
        query.setParameter(4, Direction.SOUTH);
        query.setParameter(5, new CarImpl());
        int carsNorth = spa.count(query);

        //West
        query = new SQLQuery<Roxel>(Roxel.class, "y = ? AND x <= ? AND x >= ? AND direction = ? AND occupiedBy = ?");
        x = r.getX();
        y = r.getY();
        query.setParameter(1, y);
        query.setParameter(2, x-1 < 0 ? 0 : x-1);
        query.setParameter(3, x-1-RANGE < 0 ? 0 : x-1-RANGE);
        query.setParameter(4, Direction.EAST);
        query.setParameter(5, new CarImpl());
        int carsWest = spa.count(query);


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
