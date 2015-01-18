package de.haw.battleship;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
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
	private GameState gameState;
	Strategy strategy;

	private static final int I = 100;
	private static final int S = 10;

	public void init(String[] args) {
		chord = createOrJoinChord(args);
		
		// Debug
		System.out.println("My own ID: " +chord.getID());
		Report report = (Report) chord;
		System.out.println(report.printFingerTable());

		strategy = new RandomStrategy(S, I);
	}
	
	private void initOwnFields(){
		BigInteger intervall = IdMath.calculateFieldSize(gameState.getMyPlayerMin(), gameState.getMyPlayerMax(), I);

		// Schiffe setzen und Board initialisieren
		BigInteger startRange = gameState.getMyPlayerMin().toBigInteger();
		BigInteger endRange = gameState.getMyPlayerMax().toBigInteger();
		ID aktuelPosition = gameState.getMyPlayerMin();
		ID lastPosition = gameState.getMyPlayerMax();
		
		if (startRange.min(endRange) == startRange) {
			aktuelPosition = gameState.getMyPlayerMax();
			lastPosition = gameState.getMyPlayerMin();
		}
		
		//init all fields with zero
		while(IdMath.idCompare(aktuelPosition, lastPosition) <= 0){
			gameState.setState(aktuelPosition, FieldState.WATER);
			IdMath.addToID(aktuelPosition, intervall);
		}
		
		strategy.setShips(gameState, intervall);
	}

	private boolean isBeginner() {
		BigInteger maxID = new BigInteger("2").pow(160)
				.subtract(BigInteger.ONE);
		return ID.valueOf(maxID).isInInterval(
				ID.valueOf(chord.getPredecessorID().toBigInteger()
						.add(BigInteger.ONE)), chord.getID());
	}

	private void start() {
		gameState = new GameState(IdMath.addOneToID(chord.getPredecessorID()), chord.getID(), I, S);
		initOwnFields();
		gameState.addPlayerIfNotExists(chord.getPredecessorID());
		//Init player in fingerTable
		ChordImpl c = (ChordImpl) chord;
		List<Node> fTable = c.getFingerTable();
		for (Node n : fTable) {
			gameState.addPlayerIfNotExists(n.getNodeID());
		}
		//vll noch etwas über n.findSuccessor(id) machen
		// check beginner
		if (this.isBeginner()) {
			System.out.println("Node is the first Player!");
			attack();
		} else {
			System.out.println("Waiting for the first Player!");
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
		ID target = strategy.findNextTarget(gameState, chord);
		System.out.println("Firing on " + target);
		try {
			chord.retrieve(target);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void updateInformation(ID source, ID target, Boolean hit) {
		gameState.addPlayerIfNotExists(source);
		System.out.println("Updating "+ hit +" shot to " + target);
		gameState.setState(target, hit ? FieldState.HIT : FieldState.WATER);
	}

	public void checkHit(ID target) {
		boolean hit = gameState.getFieldState(target) == FieldState.SHIP;
		System.out.println("Checking if ship was hit with " + target + " Result: " + hit);
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

	
	/*
	public static void main(String[] args) {
		PropertiesLoader.loadPropertyFile();
		
		String[] args1 = new String[2];
		args1[0] = "create";
		args1[1] = "localhost:1500";
		
		String[] args2 = new String[3];
		args2[0] = "join";
		args2[1] = "localhost:1500";
		args2[2] = "localhost:1501";
		Game game1 = new Game();
		game1.init(args1);
		
		Game game2 = new Game();
		game2.init(args2);
		System.out.println("Press key to start");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Game start");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		game2.start();
		game1.start();
		

	}*/
	
}
