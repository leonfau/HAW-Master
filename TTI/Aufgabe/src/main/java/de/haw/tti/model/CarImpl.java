package de.haw.tti.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceProperty;

import de.haw.tti.controller.GigaSpaceStreetMap;
import de.haw.tti.controller.StreetMap;

@SpaceClass
public class CarImpl implements Car, Runnable {

	private Direction direction;
	StreetMap spa;
	int initX;
	int initY;
	String color = "";

	// default constructor, required
	public CarImpl(Direction dir, int initX, int initY, String color) {
		this.direction = dir;
		spa = new GigaSpaceStreetMap("/./streetMap");
		this.initX = initX;
		this.initY = initY;
		this.color = color;
	}
	@SpaceProperty
	public Direction getDirection(){
		return direction;
	}

	@Override
	public void run() {
		Roxel currentRoxel = enterInitialRoxel(initX, initY);

		while (wantToMoveForward()) {
			moveThroughCurrRoxel(currentRoxel);
			currentRoxel = moveToNextRoxel(currentRoxel);
		}
	}
	@SpaceProperty
	public boolean isEmpty() {
		return false;
	}
	
	public String getColor(){
		return color;
	}

	private Roxel enterInitialRoxel(int x, int y) {
		Roxel r = spa.takeByCoordinate(x, y, this);
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
		return spa.takeNextRoxel(current, direction);
	}

}
