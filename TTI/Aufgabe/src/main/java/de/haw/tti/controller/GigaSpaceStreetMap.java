package de.haw.tti.controller;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import de.haw.tti.model.Direction;
import de.haw.tti.model.Roxel;

public class GigaSpaceStreetMap implements StreetMap {

	private GigaSpace space;

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
	
	
	/*************HELPER***************************/
	
	private void initStreetMap() throws Exception {
		int maxX = 6;
		int maxY = 6;

		int length = 100;
		for (int currentX = 0; currentX <= maxX; currentX++) {
			// Roxel[] xRow = new Roxel[maxY+1];

			for (int currentY = 0; currentY <= maxY; currentY++) {
				boolean xEqual = currentX % 2 == 0;
				boolean yEqual = currentY % 2 == 0;
				Roxel r = null;
				System.out.println("Init x: " + currentX + " y: " + currentY);
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
					space.write(r);
				}else{
					throw new Exception("Space not initialized");
				}
	
			}
			System.out.println("XCoord: " + currentX);
			// space.writeMultiple(xRow);
		}
	}



}
