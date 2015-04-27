package de.haw.tti.car;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceProperty;

import de.haw.tti.controller.GigaSpaceStreetMap;
import de.haw.tti.controller.StreetMap;
import de.haw.tti.exception.RoxelNotFoundException;
import de.haw.tti.model.Direction;
import de.haw.tti.model.Roxel;

@SpaceClass
public class CarImpl implements Car, Runnable {

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

	@Override
	public void run() {
		Roxel currentRoxel;
		try {
			currentRoxel = enterInitialRoxel(initX, initY);
			while (wantToMoveForward()) {
				moveThroughCurrRoxel(currentRoxel);
				currentRoxel = moveToNextRoxel(currentRoxel);
			}
		} catch (RoxelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@SpaceProperty
	public String getColor() {
		return color;
	}

	private Roxel enterInitialRoxel(int x, int y) throws RoxelNotFoundException {
		Roxel r = null;
		StreetMap spa = new GigaSpaceStreetMap("jini://*/*/streetMap");
		r = spa.takeByCoordinate(x, y, this);
		return r;
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
		StreetMap spa = new GigaSpaceStreetMap("jini://*/*/streetMap");
		try {
			return spa.takeNextRoxel(current, direction);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
