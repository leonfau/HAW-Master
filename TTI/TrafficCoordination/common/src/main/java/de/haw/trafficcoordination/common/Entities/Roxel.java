package de.haw.trafficcoordination.common.Entities;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;

@SpaceClass
public class Roxel {

	private Integer length;
	private Integer x;
	private Integer y;
	private Car occupiedBy;
	private Direction direction;
	private String id;

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
	}

	@SpaceProperty
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}
