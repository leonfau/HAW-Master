package de.haw.trafficcoordination.common.Entities;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;
import org.openspaces.events.EventTemplate;

@SpaceClass
public class CarImpl implements Car {

    /**
     *
     */
    private static final long serialVersionUID = -6856578963919981143L;

    private Direction direction;
    private int initX;
    private int initY;
    private String color = "";
    private Roxel roxel;
    private String id;

    private final long roxelTimeInMs = 200;


    // default constructor, required
    public CarImpl() {
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public CarImpl(Direction dir, int initX, int initY, String color) {
        this.direction = dir;
        this.initX = initX;
        this.initY = initY;
        this.color = color;
    }

    @SpaceProperty
    public Direction getDirection() {
        return direction;
    }


    @SpaceProperty
    public String getColor() {
        return color;
    }

    private Roxel enterInitialRoxel(int x, int y) {
        Roxel r = null;
        //	r = spa.takeByCoordinate(x, y, this);
        return null;
    }

    public Roxel getRoxel() {
        return roxel;
    }

    public void setRoxel(Roxel roxel) {
        this.roxel = roxel;
    }

    public int getInitX() {
        return initX;
    }

    public int getInitY() {
        return initY;
    }

    private boolean wantToMoveForward() {
        return true;
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
            return null;// spa.takeNextRoxel(current, direction);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public long getRoxelTimeInMs() {
        return roxelTimeInMs;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CarImpl);
    }
}
