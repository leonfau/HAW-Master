package de.haw.battleship;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	private GameState gameState;
	Strategy strategy;

	private static final int I = 100;
	private static final int S = 10;
	private BigInteger intervalSize;
	private BigInteger rangeStart;

	public void init(String[] args) {
		chord = createOrJoinChord(args);
		gameState = new GameState(IdMath.addOneToID(chord.getPredecessorID()), chord.getID());
		
		// Debug
		System.out.println(chord.getID());
		Report report = (Report) chord;
		System.out.println(report.printFingerTable());

		initOwnFields();
		gameState.addPlayerIfNotExists(chord.getPredecessorID());

		strategy = new RandomStrategy();
	}
	
	private void initOwnFields(){
		BigInteger intervall = IdMath.calculateFieldSize(gameState.getMyPlayerMin(), gameState.getMyPlayerMax(), I);
		// Schiffe setzen und Board initialisieren //TODO
		ID aktuelPosition = gameState.getMyPlayerMin();
		//init all fields with zero
		while(aktuelPosition.compareTo(gameState.getMyPlayerMax()) <= 0){
			gameState.setState(aktuelPosition, FieldState.WATER);
			IdMath.addToID(aktuelPosition, intervall);
		}
		//set ships
		for(int i = 0; i < S; i++){
			Random ran = new Random();
			int r = ran.nextInt((I - 1) + 1)+1;
			//if there is already an ship on ID try again
			ID shipID = IdMath.calcIDforField(gameState.getMyPlayerMin(), intervall, r);
			while (gameState.getFieldState(shipID) == FieldState.SHIP){
				r = ran.nextInt((I - 1) + 1)+1;
				shipID = IdMath.calcIDforField(gameState.getMyPlayerMin(), intervall, r);
			}
			gameState.setState(shipID, FieldState.SHIP);		
		}
	}

	private boolean isBeginner() {
		BigInteger maxID = new BigInteger("2").pow(160)
				.subtract(BigInteger.ONE);
		// TODO
		return ID.valueOf(maxID).isInInterval(
				ID.valueOf(chord.getPredecessorID().toBigInteger()
						.add(BigInteger.ONE)), chord.getID());
	}

	private void start() {
		//Init player in fingerTable
		ChordImpl c = (ChordImpl) chord;
		List<Node> fTable = c.getFingerTable();
		for (Node n : fTable) {
			gameState.addPlayerIfNotExists(n.getNodeID());
		}
		//vll noch etwas über n.findSuccessor(id) machen
		
		
		// check beginner
		if (this.isBeginner()) {
			System.out.println("is beginnger");
			attack();
		} else {
			System.out.println("is not beginner");
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
		
		//TODO an neues Datenmodell anpassen
		System.out.println("attack");
		ID Target = strategy.findNextTarget(enemyBoards, chord);
		try {
			chord.retrieve(Target);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void updateInformation(ID source, ID target, Boolean hit) {
		gameState.addPlayerIfNotExists(source);
		System.out.println("update");
		gameState.setState(target, hit ? FieldState.HIT : FieldState.WATER);
	}

	public void checkHit(ID target) {
		System.out.println("check hit");
		BigInteger field = target.toBigInteger().subtract(rangeStart)
				.divide(intervalSize);
		System.out.println(field);
		
		boolean hit = gameState.getFieldState(target) == FieldState.SHIP;
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
