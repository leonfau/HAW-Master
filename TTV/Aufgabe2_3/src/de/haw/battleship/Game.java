package de.haw.battleship;

import java.net.MalformedURLException;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Game {
	
	public static void main(String[] args) {
		PropertiesLoader.loadPropertyFile();
		if (args.length > 3 || args.length < 2) {
			System.out.println("Error in parameters\n");
        }
		try {
			String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
			String operation = args[0];
	        URL url = new URL(protocol + "://"+args[1]+"/");
			
	        if (operation.equals("create")) {
	        	  
	            ChordImpl chord = new ChordImpl();
	            chord.create(url);
	            
	        } else if (operation.equals("join")) {
	        	URL localURL = new URL(protocol + "://"+args[2]+"/");
	            ChordImpl chord = new ChordImpl();
	            chord.join(localURL, url);
	        }  else {
	            System.out.println("Error in parameters\n");
	        }
		} catch(MalformedURLException | ServiceException e) {
			e.printStackTrace();
		}
        
	}

}
