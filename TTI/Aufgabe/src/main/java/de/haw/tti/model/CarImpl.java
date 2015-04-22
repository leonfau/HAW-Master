package de.haw.tti.model;

import com.gigaspaces.annotation.pojo.SpaceClass;

@SpaceClass
public class CarImpl implements Car, Runnable {

	//default constructor, required
	public CarImpl() {}

	@Override
	public void run() {
		Roxel currentRoxel = enterInitialRoxel();
		
		while (wantToMoveForward()){
			moveThroughCurrRoxel();
			moveToNextRoxel(getNextRoxel());
			
			// write back as occupied (allows monitoring),
			// write previous roxel back indicated empty
		}
	}
		
	private Roxel enterInitialRoxel() {
		//TODO
		return null;
	}
	
	private boolean wantToMoveForward() {
		return true;
	}
	
	private void moveThroughCurrRoxel() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private Roxel getNextRoxel() {
		//TODO
		return null;
	}
	
	private void moveToNextRoxel(Roxel roxel) {
		
	}
}
