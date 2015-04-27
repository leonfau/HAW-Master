package de.haw.tti.controller;

import de.haw.tti.car.Car;
import de.haw.tti.exception.RoxelNotFoundException;
import de.haw.tti.exception.SpaceNotInitializedException;
import de.haw.tti.model.Direction;
import de.haw.tti.model.Roxel;

public interface StreetMap {
	public void createSpaceMap() throws SpaceNotInitializedException;
	
	public Roxel[] fetchFullMap();

	public Roxel[] fetchCars();
	
	public Roxel takeByCoordinate(int x, int y, Car car) throws RoxelNotFoundException;
	
	public Roxel takeNextRoxel(Roxel current, Direction direction) throws RoxelNotFoundException;



}
