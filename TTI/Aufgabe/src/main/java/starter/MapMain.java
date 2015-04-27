package starter;

import de.haw.tti.controller.GigaSpaceStreetMap;
import de.haw.tti.controller.StreetMap;
import de.haw.tti.exception.SpaceNotInitializedException;

public class MapMain{
	StreetMap spa;


	public static void main(String[] args) throws SpaceNotInitializedException {
		StreetMap spa = new GigaSpaceStreetMap("/./streetMap");
		spa.createSpaceMap();
	}

}
