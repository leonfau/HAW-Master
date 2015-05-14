package de.haw.trafficcoordination.common.Entities;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceProperty;


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


	// default constructor, required
	public CarImpl() {
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

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof CarImpl);	
	}
}
