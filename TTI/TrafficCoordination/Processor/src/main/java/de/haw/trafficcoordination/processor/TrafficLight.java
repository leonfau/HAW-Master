package de.haw.trafficcoordination.processor;

import de.haw.trafficcoordination.common.Entities.Direction;
import de.haw.trafficcoordination.common.Entities.Roxel;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.events.EventDriven;
import org.openspaces.events.adapter.SpaceDataEvent;

import java.util.logging.Logger;

@EventDriven
public class TrafficLight {


    Logger log = Logger.getLogger(this.getClass().getName());

    @GigaSpaceContext
    private GigaSpace spa;

    private static boolean dir = false;
    @SpaceDataEvent
    public void trafficLightListener(Roxel r) {
        log.info("Traffic Light Event Received");
        if(dir) {
            r.setDirection(Direction.EAST);
            System.out.println("EAST");
        }else{
            r.setDirection(Direction.SOUTH);
            System.out.println("SOUTHR");
        }
        dir = !dir;
        spa.write(r);
    }
}
