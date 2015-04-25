package de.haw.tti.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import de.haw.tti.controller.GigaSpaceStreetMap;
import de.haw.tti.controller.StreetMap;
import de.haw.tti.model.Car;
import de.haw.tti.model.CarImpl;
import de.haw.tti.model.Direction;
import de.haw.tti.model.Roxel;

public class Main extends BasicGame {
	StreetMap spa;
	private Roxel[] map;

	public Main(String title) {
		super(title);
		spa = new GigaSpaceStreetMap("/./streetMap");
		spa.createSpaceMap();
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		map = spa.fetchFullMap();

		Car car = new CarImpl(Direction.EAST, 0, 5, "black");
		(new Thread((CarImpl) car)).start();
		Car car2 = new CarImpl(Direction.EAST, 1, 5, "red");
		(new Thread((CarImpl) car2)).start();
		Car car5 = new CarImpl(Direction.EAST, 2, 5, "blue");
		(new Thread((CarImpl) car5)).start();
		
		Car car10 = new CarImpl(Direction.EAST, 3, 5, "green");
		(new Thread((CarImpl) car10)).start();
		Car car11 = new CarImpl(Direction.EAST, 4, 5, "white");
		(new Thread((CarImpl) car11)).start();
		Car car12 = new CarImpl(Direction.EAST, 5, 5, "black");
		(new Thread((CarImpl) car12)).start();
		
		Car car3 = new CarImpl(Direction.SOUTH, 1, 0, "red");
		(new Thread((CarImpl) car3)).start();
		Car car4 = new CarImpl(Direction.SOUTH, 3, 3, "blue");
		(new Thread((CarImpl) car4)).start();
		Car car6 = new CarImpl(Direction.SOUTH, 5, 0, "green");
		(new Thread((CarImpl) car6)).start();
		
		Car car7 = new CarImpl(Direction.SOUTH, 1, 0, "white");
		(new Thread((CarImpl) car7)).start();
		Car car8 = new CarImpl(Direction.SOUTH, 3, 3, "black");
		(new Thread((CarImpl) car8)).start();
		Car car9 = new CarImpl(Direction.SOUTH, 5, 0, "red");
		(new Thread((CarImpl) car9)).start();
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
			if (!map[i].getOccupiedBy().isEmpty()) {
				img = new Image("assets/car-"+ ((CarImpl)map[i].getOccupiedBy()).getColor() +".png");
				
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
			appgc = new AppGameContainer(new Main("TTI Praktikum"));
			appgc.setDisplayMode(700, 700, false);
			appgc.setShowFPS(false);
			appgc.start();
		} catch (SlickException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
