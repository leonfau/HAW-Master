package de.haw.tti.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;

import de.haw.tti.car.Car;
import de.haw.tti.car.EmptyCar;

@SpaceClass
public class Roxel {

	private Integer length;
	private Integer x;
	private Integer y;
	private Car occupiedBy;
	private Direction direction;
	private String id;
	private boolean isOccupied;

	// default constructor, required
	public Roxel() {
	}

	public Roxel(Integer length, Integer x, Integer y, Direction direction) {
		super();
		this.length = length;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.occupiedBy = new EmptyCar();
		//this.isOccupied = false;
	}

	// @SpaceId
	// public String getUid() {
	// return String.valueOf(x) + String.valueOf(y);
	// }

	@SpaceId(autoGenerate = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SpaceProperty
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@SpaceProperty
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	@SpaceProperty
	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@SpaceProperty
	public Car getOccupiedBy() {
		return occupiedBy;
	}

	public void setOccupiedBy(Car occupiedBy) {
		this.occupiedBy = occupiedBy;
		if (occupiedBy instanceof EmptyCar) {
			this.isOccupied = false;
		} else {
			this.isOccupied = true;
		}
	}

	@SpaceProperty
	public boolean getIsOccupied() {
		return isOccupied;
	}

	public void setIsOccupied(boolean occupied) {
		this.isOccupied = occupied;
	}

	@SpaceProperty
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}
