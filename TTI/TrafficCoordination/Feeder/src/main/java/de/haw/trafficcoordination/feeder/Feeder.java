package de.haw.trafficcoordination.feeder;


import de.haw.trafficcoordination.common.Entities.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
    private static final int X_SIZE = 20;
    private static final int Y_SIZE = 20;
    private static final int ROXEL_SIZE = 50;
    private static final int CAR_AMOUNT = 50;


    @GigaSpaceContext
    private GigaSpace gigaSpace;


    public void afterPropertiesSet() throws Exception {
        log.info("--- Create World Map");
//        executorService = Executors.newScheduledThreadPool(1);
        Roxel[] map = this.createMap();
        log.info("---" + map.length + " Roxel");
        gigaSpace.writeMultiple(map);
        log.info(gigaSpace.readMultiple(new Roxel()).length + " found");

        this.createRandomCars(CAR_AMOUNT);
    }

    public void destroy() throws Exception {
        sf.cancel(false);
        sf = null;
        this.executorService.shutdown();
    }


    private Roxel[] createMap() {
        int roxelCount = (X_SIZE + 1) * (Y_SIZE + 1);
        Roxel map[] = new Roxel[roxelCount];
        int i = 0;
        for (int currentX = 0; currentX <= X_SIZE; currentX++) {
            for (int currentY = 0; currentY <= Y_SIZE; currentY++) {
                boolean xEqual = currentX % 2 == 0;
                boolean yEqual = currentY % 2 == 0;
                Roxel r = null;
                if (xEqual && yEqual) {
                    r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.BLOCKED);
                } else if (!xEqual && yEqual) {
                    r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.SOUTH);
                } else if (xEqual && !yEqual) {
                    r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.EAST);
                } else if (!xEqual && !yEqual) {
                    r = new Roxel(ROXEL_SIZE, currentX, currentY,
                            Direction.TODECIDE);
                }
                r.setOccupiedBy(new EmptyCar());
                map[i++] = r;
            }
        }
        return map;
    }

    private void createRandomCars(int amount) {
        Random random = new Random();
        List<String> colors = new ArrayList<String>(Arrays.asList("black", "blue", "green", "red", "white"));


        for (; amount > 0; amount--) {
            int x = random.nextInt(X_SIZE);
            int y = random.nextInt(Y_SIZE);
            Direction dir = null;

            while (x % 2 == 0 && y % 2 == 0)
            {
                if(random.nextBoolean()){
                    x = ((x+1)% X_SIZE);
                }else{
                    y = ((y+1)% X_SIZE);
                }
            }

            if(x%2 != 0) {
                dir = Direction.SOUTH;
            }else{
                dir = Direction.EAST;

            }

            Car car = new CarImpl(dir, x, y, colors.get(random.nextInt(colors.size() - 1)));
            new Thread((new CarThread((CarImpl) car))).start();
        }
    }
}