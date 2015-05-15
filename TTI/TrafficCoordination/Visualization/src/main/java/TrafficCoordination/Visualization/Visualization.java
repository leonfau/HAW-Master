package TrafficCoordination.Visualization;

import de.haw.trafficcoordination.common.Entities.CarImpl;
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

    public Visualization() {
        super("Traffic Coordination");
        this.initSpace("jini://*/*/space");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        map = this.fetchFullMap();
        System.out.println("Roxel: " + map.length);

    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        // TODO Call logic

    }

    private void drawMap() throws SlickException {
        for (int i = 0; i < map.length; i++) {
            double size = map[i].getLength();
            double xCoord = map[i].getX() * size;
            double yCoord = map[i].getY() * size;
            Image img = null;
            switch (map[i].getDirection()) {
                case BLOCKED:
                    img = new Image("images/blocked.png");
                    break;
                case SOUTH:
                    img = new Image("images/street.png");
                    img.setRotation(90);
                    break;
                case EAST:
                    img = new Image("images/street.png");
                    break;
                case TODECIDE:
                    img = new Image("images/todecide.png");
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
            double size = cars[i].getLength();
            double xCoord = cars[i].getX() * size;
            double yCoord = cars[i].getY() * size;
            Image img = null;
            img = new Image("images/car-"
                    + ((CarImpl) cars[i].getOccupiedBy()).getColor() + ".png");
            switch (((CarImpl)cars[i].getOccupiedBy()).getDirection()) {
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

    public void render(GameContainer gc, Graphics g) throws SlickException {
        // @TODO: nur noch in init, nur noch autos abfragen

        cars = this.fetchCars();

        //System.out.println(cars.length);

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

    private void initSpace(String url){
        UrlSpaceConfigurer spaceConfigurer = new UrlSpaceConfigurer(url);
        this.spa = new GigaSpaceConfigurer(spaceConfigurer).gigaSpace();
        if (this.spa == null) System.err.println("GigaSpace nicht gefunden!");
    }

    private static void start() throws SlickException {
        AppGameContainer app = new AppGameContainer(new Visualization());
        app.setDisplayMode(700, 700, false);
      //  app.setTargetFrameRate(FRAMERATE);
      //  app.setMaximumLogicUpdateInterval(FRAMERATE);
      //  app.setForceExit(false);
      //  app.setVSync(true);
        app.start();
    }

    public static void main(String[] args) throws SlickException {
        start();
    }
}
