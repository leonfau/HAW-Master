package de.haw.tti.controller;

import de.haw.tti.model.Roxel;

public interface StreetMap {
	public void createSpaceMap();
	
	public Roxel[] fetchFullMap();

}
