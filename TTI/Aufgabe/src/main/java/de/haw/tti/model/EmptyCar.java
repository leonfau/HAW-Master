package de.haw.tti.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class EmptyCar implements Car {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7720110171982923436L;
	private String id;
	// default constructor, required
	public EmptyCar() {
	}
	
	@SpaceId(autoGenerate = true)
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

}
