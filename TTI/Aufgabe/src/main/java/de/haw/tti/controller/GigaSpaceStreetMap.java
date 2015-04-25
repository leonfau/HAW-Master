package de.haw.tti.controller;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import de.haw.tti.model.Car;
import de.haw.tti.model.Direction;
import de.haw.tti.model.EmptyCar;
import de.haw.tti.model.Roxel;

public class GigaSpaceStreetMap implements StreetMap {

	private GigaSpace space;
	private final int MAX_BLOCK = 5000;

	public GigaSpaceStreetMap(String url) {
		super();
		space = new GigaSpaceConfigurer(new UrlSpaceConfigurer(url))
				.gigaSpace();
	}

	public void createSpaceMap() {
		try {
			this.initStreetMap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Roxel[] fetchFullMap() {
		Roxel[] map = space.readMultiple(new Roxel());
		return map;
	}

	public Roxel[] fetchCars() {
		// @TODO: Wrong fetches only EmptyCars
		Roxel r = new Roxel();
		r.setOccupiedBy(EmptyCar.getInstance());
		return space.readMultiple(r);
	}

	public Roxel takeByCoordinate(int x, int y, Car car) {
		Roxel query = new Roxel();
		query.setX(x);
		query.setY(y);
		query.setOccupiedBy(EmptyCar.getInstance());

		Roxel next =  space.take(query, MAX_BLOCK);
		next.setOccupiedBy(car);
		space.write(next);
		return next;
	}

	public Roxel takeNextRoxel(Roxel current, Direction direction) {
		Roxel query = new Roxel();
		query.setX(0);
		
		int roxelMaxX = space.count(query);
		query.setX(null);
		query.setY(0);
		int roxelMaxY = space.count(query);

		switch (direction) {
		case EAST: 
			query.setX((current.getX()+1)%roxelMaxX);
			query.setY(current.getY());
			query.setOccupiedBy(EmptyCar.getInstance());
			break;
		case SOUTH: 
			query.setX(current.getX());
			query.setY((current.getY()+1)%roxelMaxY);
			query.setOccupiedBy(EmptyCar.getInstance());
			break;
		default:

		}
		Roxel next = space.take(query,MAX_BLOCK);

		next.setOccupiedBy(current.getOccupiedBy());
		space.write(next);
		
		current.setOccupiedBy(EmptyCar.getInstance());
		space.write(current);
		
		return next;

	}

	/************* HELPER ***************************/

	private void initStreetMap() throws Exception {
		int maxX = 6;
		int maxY = 6;

		int length = 100;
		for (int currentX = 0; currentX <= maxX; currentX++) {
			for (int currentY = 0; currentY <= maxY; currentY++) {
				boolean xEqual = currentX % 2 == 0;
				boolean yEqual = currentY % 2 == 0;
				Roxel r = null;
				if (xEqual && yEqual) {
					r = new Roxel(length, currentX, currentY, Direction.BLOCKED);
				} else if (!xEqual && yEqual) {
					r = new Roxel(length, currentX, currentY, Direction.SOUTH);
				} else if (xEqual && !yEqual) {
					r = new Roxel(length, currentX, currentY, Direction.EAST);
				} else if (!xEqual && !yEqual) {
					r = new Roxel(length, currentX, currentY,
							Direction.TODECIDE);
				}
				// xRow[currentY] = r;
				if (space != null) {
					r.setOccupiedBy(EmptyCar.getInstance());
					space.write(r);
				} else {
					throw new Exception("Space not initialized");
				}
			}
			// space.writeMultiple(xRow);
		}
	}

}
