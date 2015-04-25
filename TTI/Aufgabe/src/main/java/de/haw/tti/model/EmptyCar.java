package de.haw.tti.model;

import com.gigaspaces.annotation.pojo.SpaceClass;

@SpaceClass
public class EmptyCar implements Car {
	private static Car empty = null;

	// default constructor, required
	private EmptyCar() {
	}

	public static Car getInstance(){
		if (empty== null){
			empty = new EmptyCar();
		}
		return empty;
	}

	public boolean isEmpty() {
		return true;
	}
}
