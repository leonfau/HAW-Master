package de.haw.tti.controller;

import net.jini.core.lease.Lease;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import de.haw.tti.car.Car;
import de.haw.tti.car.EmptyCar;
import de.haw.tti.exception.RoxelNotFoundException;
import de.haw.tti.exception.SpaceNotInitializedException;
import de.haw.tti.model.Direction;
import de.haw.tti.model.Roxel;

public class GigaSpaceStreetMap implements StreetMap {

	private GigaSpace space;
	private final int MAX_BLOCK = 500000;
	private EmptyCar emptyCar;

	public GigaSpaceStreetMap(String url) {
		super();
		space = new GigaSpaceConfigurer(new UrlSpaceConfigurer(url))
				.gigaSpace();

		emptyCar = space.read(new EmptyCar());

	}

	public void createSpaceMap() throws SpaceNotInitializedException {

		this.initEmptyCar();
		this.initStreetMap(emptyCar);
	}

	public Roxel[] fetchFullMap() {
		Roxel q = new Roxel();
		q.setIsOccupied(true);
		Roxel[] occ = space.readMultiple(q);
		q.setIsOccupied(false);
		Roxel[] map = space.readMultiple(q);

		Roxel[] result = new Roxel[occ.length + map.length];
		System.arraycopy(occ, 0, result, 0, occ.length);
		System.arraycopy(map, 0, result, occ.length, map.length);
		return result;
	}

	public Roxel[] fetchCars() {
		// @TODO: Wrong fetches only EmptyCars
		Roxel r = new Roxel();
		r.setOccupiedBy(new EmptyCar());
		return space.readMultiple(r);
	}

	public Roxel takeByCoordinate(int x, int y, Car car)
			throws RoxelNotFoundException {
		Roxel query = new Roxel();
		query.setX(x);
		query.setY(y);
		// query.setOccupiedBy(emptyCar);
		query.setIsOccupied(false);

		Roxel next = space.take(query, MAX_BLOCK);
		if (next == null) {
			throw new RoxelNotFoundException("roxel x: " + query.getX() + " y: " +  query.getY());
		}
		next.setOccupiedBy(car);
		space.write(next);
		return next;
	}

	public Roxel takeNextRoxel(Roxel current, Direction direction)
			throws RoxelNotFoundException {
		Roxel query = new Roxel();
		query.setX(0);

		int roxelMaxX = 7;// space.count(query);
		query.setX(null);
		query.setY(0);
		int roxelMaxY = 7;// space.count(query);

		switch (direction) {
		case EAST:
			query.setX((current.getX() + 1) % roxelMaxX);
			query.setY(current.getY());
			break;
		case SOUTH:
			query.setX(current.getX());
			query.setY((current.getY() + 1) % roxelMaxY);
			break;
		default:

		}
		query.setIsOccupied(false);

		Roxel next = space.take(query, MAX_BLOCK);

		if (next == null) {
			throw new RoxelNotFoundException("roxel not found");

		}
		Car c = current.getOccupiedBy();
		next.setOccupiedBy(c);
		space.write(next);

		current.setOccupiedBy(emptyCar);
		space.write(current);

		return next;

	}

	/************* HELPER ***************************/

	private void initStreetMap(EmptyCar empty)
			throws SpaceNotInitializedException {
		int maxX = 6;
		int maxY = 6;

		int length = 100;
		int roxelCount = (maxX + 1) * (maxY + 1);
		Roxel map[] = new Roxel[roxelCount];
		int i = 0;
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
				r.setOccupiedBy(empty);
				map[i++] = r;
			}
		}
		if (space == null) {
			throw new SpaceNotInitializedException("space is null");
		}
		space.writeMultiple(map);
	}

	private void initEmptyCar() throws SpaceNotInitializedException {
		if (space == null) {
			throw new SpaceNotInitializedException("space is null");
		}
		EmptyCar empty = new EmptyCar();
		space.write(empty, Lease.FOREVER);
		this.emptyCar =  empty;
	}

}
