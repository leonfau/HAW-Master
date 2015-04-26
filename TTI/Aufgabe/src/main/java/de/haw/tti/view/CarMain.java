package de.haw.tti.view;

import de.haw.tti.model.Car;
import de.haw.tti.model.CarImpl;
import de.haw.tti.model.Direction;

public class CarMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Car car = new CarImpl(Direction.EAST, 0, 3, "black");
		(new Thread((CarImpl) car)).start();
		Car car2 = new CarImpl(Direction.EAST, 1, 3, "red");
		(new Thread((CarImpl) car2)).start();
		//Car car5 = new CarImpl(Direction.EAST, 2, 5, "blue");
		//(new Thread((CarImpl) car5)).start();

		Car car10 = new CarImpl(Direction.EAST, 3, 3, "green");
		(new Thread((CarImpl) car10)).start();
		Car car11 = new CarImpl(Direction.EAST, 4, 3, "white");
		(new Thread((CarImpl) car11)).start();
		Car car12 = new CarImpl(Direction.EAST, 5, 3, "black");
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

}
