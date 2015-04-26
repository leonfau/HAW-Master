package de.haw.tti.view;

import de.haw.tti.controller.GigaSpaceStreetMap;
import de.haw.tti.controller.StreetMap;

public class MapMain{
	StreetMap spa;


	public static void main(String[] args) {
		StreetMap spa = new GigaSpaceStreetMap("/./streetMap");
		spa.createSpaceMap();
	}

}
