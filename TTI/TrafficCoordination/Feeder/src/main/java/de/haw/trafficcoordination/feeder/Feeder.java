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
    private static final int X_SIZE = 20;
    private static final int Y_SIZE = 20;
    private static final int ROXEL_SIZE = 50;
    private static final int CAR_AMOUNT = 30;


    @GigaSpaceContext
    private GigaSpace gigaSpace;


    public void afterPropertiesSet() throws Exception {
        log.info("--- Create World Map");
        executorService = Executors.newScheduledThreadPool(1);
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
        int roxelCount = (X_SIZE) * (Y_SIZE);
        Roxel map[] = new Roxel[roxelCount];
        int i = 0;
        int tilenr = 0;
        for (int currentX = 0; currentX < X_SIZE; currentX++) {
            for (int currentY = 0; currentY < Y_SIZE; currentY++) {
                int xPos = currentX % 3;
                int yPos = currentY % 3;
                Roxel r = null;
                if (yPos != 2) {
                    if (xPos != 2) {
                        r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.BLOCKED, tilenr);
                    } else {
                        r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.SOUTH, tilenr);
                    }
                } else
                    if (xPos != 2) {
                    r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.EAST, tilenr);
                } else {
                    r = new Roxel(ROXEL_SIZE, currentX, currentY, Direction.TODECIDE, tilenr);
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

            while (x % 3 != 0)
            {
                x = random.nextInt(X_SIZE);
            }
            
            while (y % 3 != 0) {
                y = random.nextInt(Y_SIZE);
            }

            if(random.nextBoolean()) {
                dir = Direction.SOUTH;
            }else{
                dir = Direction.EAST;

            }
            x--;
            y--;


            Car car = new CarImpl(dir, x, y, colors.get(random.nextInt(colors.size() - 1)));


            log.info("--- Starting Car Thread with delay " + "");

            this.executorService = Executors.newScheduledThreadPool(1);
            CarThread thread = new CarThread((CarImpl) car);
            (new Thread(thread)).start();
         //   this.sf = this.executorService.scheduleAtFixedRate(thread, 20, 20,
         //           TimeUnit.MILLISECONDS);
        }
    }
}