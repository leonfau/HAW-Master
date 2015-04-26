package de.haw.tti.controller;

import de.haw.tti.model.Car;
import de.haw.tti.model.Direction;
import de.haw.tti.model.Roxel;

public interface StreetMap {
	public void createSpaceMap();
	
	public Roxel[] fetchFullMap();

	public Roxel[] fetchCars();
	
	public Roxel takeByCoordinate(int x, int y, Car car) throws Exception;
	
	public Roxel takeNextRoxel(Roxel current, Direction direction) throws Exception;



}
