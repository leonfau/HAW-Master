package de.haw.trafficcoordination.feeder;

import de.haw.trafficcoordination.common.Data;

import de.haw.trafficcoordination.common.Entities.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.SpaceInterruptedException;
import org.openspaces.core.context.GigaSpaceContext;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A feeder bean starts a scheduled task that writes a new Data objects to the space
 * (in an unprocessed state).
 * <p>
 * <p>The space is injected into this bean using OpenSpaces support for @GigaSpaceContext
 * annotation.
 * <p>
 * <p>The scheduling uses the java.util.concurrent Scheduled Executor Service. It
 * is started and stopped based on Spring life cycle events.
 *
 * @author kimchy
 */
public class Feeder implements InitializingBean, DisposableBean {

    Logger log = Logger.getLogger(this.getClass().getName());


    private ScheduledFuture<?> sf;
    private ScheduledExecutorService executorService;



    @GigaSpaceContext
    private GigaSpace gigaSpace;


    public void afterPropertiesSet() throws Exception {
        log.info("--- Create World Map");
//        executorService = Executors.newScheduledThreadPool(1);
        Roxel[] map = this.createMap();
        log.info("---" + map.length + " Roxel");
        gigaSpace.writeMultiple(map);
        log.info(gigaSpace.readMultiple(new Roxel()).length + " found");
        /***Test***/
        Car car = new CarImpl(Direction.EAST, 0, 1, "black");
        new Thread((new CarThread((CarImpl) car))).start();
    }

    public void destroy() throws Exception {
        sf.cancel(false);
        sf = null;
        this.executorService.shutdown();
    }


    private Roxel[] createMap() {
        int maxX = 20;
        int maxY = 20;

        int length = 50;
        int roxelCount = (maxX + 1) * (maxY + 1);
        Roxel map[] = new Roxel[roxelCount];
        int i = 0;
        for (int currentX = 0; currentX <= maxX; currentX++) {
            for (int currentY = 0; currentY <= maxY; currentY++) {
                boolean xEqual = currentX % 2 == 0;
                boolean yEqual = currentY % 2 == 0;
                Roxel r = null;
                if (xEqual && yEqual) {
                    r = new Roxel(length, currentX, currentY, Direction.BLOCKED);
                } else if (!xEqual && yEqual) {
                    r = new Roxel(length, currentX, currentY, Direction.SOUTH);
                } else if (xEqual && !yEqual) {
                    r = new Roxel(length, currentX, currentY, Direction.EAST);
                } else if (!xEqual && !yEqual) {
                    r = new Roxel(length, currentX, currentY,
                            Direction.TODECIDE);
                }
                r.setOccupiedBy(new EmptyCar());
                map[i++] = r;
            }
        }
        return map;
    }
}