package TrafficCoordination.Visualization;

import de.haw.trafficcoordination.common.Entities.CarImpl;
import de.haw.trafficcoordination.common.Entities.Direction;
import de.haw.trafficcoordination.common.Entities.Roxel;
import org.newdawn.slick.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * A Simulation using Slick2d
 */
public class Visualization extends BasicGame implements InitializingBean, DisposableBean {
    @GigaSpaceContext(name = "gigaSpace")
    private GigaSpace spa;

    private Roxel[] map;
    private Roxel[] cars;
    private Image[] basicImages;

    public Visualization() {
        super("Traffic Coordination");
        this.initSpace("jini://*/*/space");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        map = this.fetchFullMap();
        cars = fetchCars();
        basicImages = new Image[3];
        basicImages[0] = new Image("images/street.png");
        basicImages[1] = new Image("images/blocked.png");
        basicImages[2] = new Image("images/todecide.png");
        System.out.println("Roxel: " + map.length);

    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        cars = this.fetchCars();

    }

    private void drawMap() throws SlickException {
        for (int i = 0; i < map.length; i++) {
            double size = map[i].getLength();
            double xCoord = map[i].getX() * size;
            double yCoord = map[i].getY() * size;
            Image img = null;
            switch (map[i].getTrafficLightDirection()) {
                case BLOCKED:
                    img = basicImages[1].copy();
                    break;
                case SOUTH:
                    img =  basicImages[0].copy();
                    img.setRotation(90);
                    break;
                case EAST:
                    img =  basicImages[0].copy();
                    break;
                case TODECIDE:
                    img = basicImages[2].copy();
                    break;
                default:
                    break;
            }

            if (img != null) {
                img.draw(new Float(xCoord), new Float(yCoord));
            } else {
                System.out.println("img null");
            }
        }

        Roxel[] tdc = this.fetchTODECIDE();
        for (int i = 0; i < tdc.length; i++) {
            double size = tdc[i].getLength();
            double xCoord = tdc[i].getX() * size;
            double yCoord = tdc[i].getY() * size;
            Image img = null;
            switch (tdc[i].getDirection()) {
                case BLOCKED:
                    img = basicImages[1].copy();
                    break;
                case SOUTH:
                    img =  basicImages[0].copy();
                    img.setRotation(90);
                    break;
                case EAST:
                    img =  basicImages[0].copy();
                    break;
                case TODECIDE:
                    img = basicImages[2].copy();
                    break;
                default:
                    break;
            }

            if (img != null) {
                img.draw(new Float(xCoord), new Float(yCoord));
            } else {
                System.out.println("img null");
            }
        }


        for (int i = 0; i < cars.length; i++) {
            if (cars[i].getOccupiedBy() instanceof CarImpl) {
                double size = cars[i].getLength();
                double xCoord = cars[i].getX() * size;
                double yCoord = cars[i].getY() * size;
                Image img = null;
                img = new Image("images/car-"
                        + ((CarImpl) cars[i].getOccupiedBy()).getColor() + ".png");
                switch (((CarImpl) cars[i].getOccupiedBy()).getDirection()) {
                    case SOUTH:
                        img.setRotation(90);
                        break;
                    default:
                        break;
                }
                if (img != null) {
                    img.draw(new Float(xCoord), new Float(yCoord));
                } else {
                    System.out.println("img null");
                }
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        this.drawMap();
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    private Roxel[] fetchFullMap() {
        Roxel q = new Roxel();
        return spa.readMultiple(q);
    }

    private Roxel[] fetchCars() {
        Roxel r = new Roxel();
        r.setOccupiedBy(new CarImpl());
        return spa.readMultiple(r);
    }

    private Roxel[] fetchTODECIDE() {
        Roxel r = new Roxel();
        r.setTrafficLightDirection(Direction.TODECIDE);
        return spa.readMultiple(r);
    }

    private void initSpace(String url){

        UrlSpaceConfigurer spaceConfigurer = new UrlSpaceConfigurer(url);
        this.spa = new GigaSpaceConfigurer(spaceConfigurer).gigaSpace();

        if (this.spa == null) System.err.println("GigaSpace nicht gefunden!");
    }

    private static void start() throws SlickException {
        AppGameContainer app = new AppGameContainer(new Visualization());
        app.setDisplayMode(1000, 1000, false);
      //  app.setTargetFrameRate(FRAMERATE);
      //  app.setMaximumLogicUpdateInterval(FRAMERATE);
      //  app.setForceExit(false);
       // app.setVSync(true);
        app.start();
    }

    public static void main(String[] args) throws SlickException {
        start();
    }
}
