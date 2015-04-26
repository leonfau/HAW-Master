package de.haw.tti.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import de.haw.tti.controller.GigaSpaceStreetMap;
import de.haw.tti.controller.StreetMap;
import de.haw.tti.model.CarImpl;
import de.haw.tti.model.Roxel;

public class Viewer extends BasicGame {
	StreetMap spa;
	private Roxel[] map;

	public Viewer(String title) {
		super(title);
		spa = new GigaSpaceStreetMap("jini://*/*/streetMap");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		map = spa.fetchFullMap();

	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		// TODO Call logic

	}

	private void drawMap() throws SlickException {
		for (int i = 0; i < map.length; i++) {
			double size = map[i].getLength();
			double xCoord = map[i].getX() * size;
			double yCoord = map[i].getY() * size;
			Image img = null;
			
			if (map[i].getIsOccupied()) {
				System.out.println("Occupied: " + xCoord + ":" + yCoord);

				System.out.println("Hallo" + map[i].getIsOccupied());
				System.out.println(i);
				System.out.println("assets/car-"
						+ ((CarImpl) map[i].getOccupiedBy()).getColor()
						+ ".png");
				img = new Image("assets/car-"
						+ ((CarImpl) map[i].getOccupiedBy()).getColor()
						+ ".png");

				switch (((CarImpl) map[i].getOccupiedBy()).getDirection()) {
				case SOUTH:
					img.setRotation(90);
					break;
				default:
					break;
				}

			} else {
				switch (map[i].getDirection()) {
				case BLOCKED:
					img = new Image("assets/blocked.png");
					break;
				case SOUTH:
					img = new Image("assets/street.png");
					img.setRotation(90);
					break;
				case EAST:
					img = new Image("assets/street.png");
					break;
				case TODECIDE:
					img = new Image("assets/todecide.png");
					break;
				default:
					break;
				}
			}
			if (img != null) {
				img.draw(new Float(xCoord), new Float(yCoord));
			}else{
				System.out.println("img null");
			}
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		// @TODO: nur noch in init, nur noch autos abfragen
		 map = spa.fetchFullMap();
		
		// Roxel[] cars = spa.fetchCars();
		
		this.drawMap();

	}

	public static void main(String[] args) {
		try {
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Viewer("TTI Praktikum"));
			appgc.setDisplayMode(700, 700, false);
			appgc.setShowFPS(false);
			appgc.start();
		} catch (SlickException ex) {
			Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
