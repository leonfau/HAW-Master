package de.haw.tti.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class Roxel {

	private Integer uid;
    private Integer length; 
    private Integer x; 
    private Integer y;
    private Car occupiedBy;
    private Direction direction;
    
    //default constructor, required
    public Roxel() {}

	public Roxel(Integer uid, Integer length, Integer x, Integer y, Direction direction) {
		super();
		this.uid = uid;
		this.length = length;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.occupiedBy = new EmptyCar();
	}

	@SpaceId
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Car getOccupiedBy() {
		return occupiedBy;
	}

	public void setOccupiedBy(Car occupiedBy) {
		this.occupiedBy = occupiedBy;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	} 

}
