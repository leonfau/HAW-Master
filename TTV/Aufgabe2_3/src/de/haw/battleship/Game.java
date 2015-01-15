package de.haw.battleship;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;


import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.Report;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Game {

	private Chord chord;
	private BoardState ownBoard;
	private Map<ID, BoardState> enemyBoards;
	Strategy strategy;

	private static final int I = 100;
	private static final int S = 10;



	public void init(String[] args) {
		chord = createOrJoinChord(args);
		

		// Debug
		System.out.println(chord.getID());
		Report report = (Report) chord;
		System.out.println(report.printFingerTable());

		// Schiffe setzen und Board initialisieren //TODO
		ownBoard = new BoardState(I, FieldState.WATER);

		int i = 0;
		Random ran = new Random();
		while (i != S) {
			int r = ran.nextInt(I-1)+1;
			if (ownBoard.getPositionState(r) != FieldState.SHIP) {
				ownBoard.setShip(r);
				i++;
			}
		}

		enemyBoards = new HashMap<ID, BoardState>();

		strategy = new RandomStrategy();
	}
	
	private boolean isBeginner(){
		BigInteger maxID = new BigInteger("2").pow(160).subtract(BigInteger.ONE);
		//TODO
		return false;
	}

	private void start() {
		// check beginner 
		if(this.isBeginner()){
			System.out.println("is beginnger");
			attack();
		}
		
	}

	private Chord createOrJoinChord(String[] args) {
		PropertiesLoader.loadPropertyFile();
		if (args.length > 3 || args.length < 2) {
			System.out.println("Error in parameters\n");
		}
		try {
			String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
			String operation = args[0];
			URL url = new URL(protocol + "://" + args[1] + "/");
			chord = new ChordImpl();
			NotifyCallback ncb = new NotifyCallbackImpl(this);
			chord.setCallback(ncb);
			if (operation.equals("create")) {
				chord.create(url);

			} else if (operation.equals("join")) {
				URL localURL = new URL(protocol + "://" + args[2] + "/");
				chord.join(localURL, url);
			} else {
				System.out.println("Error in parameters\n");
			}
			return chord;
		} catch (MalformedURLException | ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void attack() {
		System.out.println("attack");
		ID Target = strategy.findNextTarget(enemyBoards, chord);
		try { 
			chord.retrieve(Target);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void updateInformation(ID source, ID target, Boolean hit) {
		System.out.println("update");
		BoardState targetBoard = enemyBoards.get(target);
		if(targetBoard == null){
			targetBoard = new BoardState(I, FieldState.UNKNOWN);
		}
		
		//Wo auf das Board wurde geschossen?
		
		enemyBoards.put(target, targetBoard);
	}

	public void checkHit(ID target) {
		System.out.println("check hit");

		// check if Hit
		boolean hit = false;
		// Broadcast result
		chord.broadcast(target, hit);
		attack();
	}

	// create localhost:1500
	// join localhost:1500 localhost:1501
	public static void main(String[] args) {
		Game game = new Game();
		game.init(args);
		System.out.println("Press key to start");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Game start");
		game.start();

	}

}