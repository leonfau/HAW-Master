package de.haw.trafficcoordination.common.Entities;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.annotation.pojo.SpaceProperty;

import java.io.Serializable;

@SpaceClass
public class Roxel implements Serializable {

	private Integer length;
	private Integer x;
	private Integer y;
	private Car occupiedBy;
	private Direction direction;
	private String id;
	private Direction trafficLightDirection;

	private Integer tileNR;

	// default constructor, required
	public Roxel() {
	}

	public Roxel(Integer length, Integer x, Integer y, Direction direction, Integer tileNR) {
		super();
		this.length = length;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.tileNR = tileNR;
		this.occupiedBy = new EmptyCar();
		this.trafficLightDirection = direction;
	}

	@SpaceId(autoGenerate = true)
	public String getId() {
		return id;
	}


	@SpaceRouting
	public Integer getTileNR()
	{
		return tileNR;
	}

	public void setTileNR(Integer tileNr)
	{
		 this.tileNR = tileNR;
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

	public void setTrafficLightDirection(Direction trafficLightDirection) {
		this.trafficLightDirection = trafficLightDirection;
	}

	@SpaceProperty
	public Direction getTrafficLightDirection() {
		return trafficLightDirection;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Roxel roxel = (Roxel) o;

		if (length != null ? !length.equals(roxel.length) : roxel.length != null) return false;
		if (x != null ? !x.equals(roxel.x) : roxel.x != null) return false;
		if (y != null ? !y.equals(roxel.y) : roxel.y != null) return false;
		if (occupiedBy != null ? !occupiedBy.equals(roxel.occupiedBy) : roxel.occupiedBy != null) return false;
		if (direction != roxel.direction) return false;
		if (id != null ? !id.equals(roxel.id) : roxel.id != null) return false;
		return !(tileNR != null ? !tileNR.equals(roxel.tileNR) : roxel.tileNR != null);
	}

	@Override
	public int hashCode() {
		int result = length != null ? length.hashCode() : 0;
		result = 31 * result + (x != null ? x.hashCode() : 0);
		result = 31 * result + (y != null ? y.hashCode() : 0);
		result = 31 * result + (occupiedBy != null ? occupiedBy.hashCode() : 0);
		result = 31 * result + (direction != null ? direction.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (tileNR != null ? tileNR.hashCode() : 0);
		return result;
	}
}
