/**
 * Technik & Technologie vernetzter Systeme
 * Teil 2: P2P-Kommunikation: Chord mit Broadcast (3. & 4. Praktikum)
 * Projekt: Implementierung eines verteilten Spiels "Schiffe Versenken" (ohne Churn).
 * 
 * @author Erwin Lang, Leon Fausten
 *
 */
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
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * Game logic
 * 
 *
 */
public class Game {

	private Chord chord;
	private GameState gameState;
	Strategy strategy;

	/**
	 * Field size
	 */	
	private static final int I = 10;
	
	/**
	 * Ship count
	 */
	private static final int S = 3;

	/**
	 * Initialization of chord network and strategy
	 * 
	 * Create new game
	 * @param args[0] create
	 * @param args[1] ownIP:Port
	 * 
	 * Join existing game
	 * @param args[0] join
	 * @param args[1] ownIP:Port
	 * @param args[2] opponentIP:Port
	 */
	public void init(String[] args) {
		chord = createOrJoinChord(args);
		strategy = new RandomStrategy(S, I);
	}
	
	
	/**
	 * Set WATER State and place own Ships in own field
	 */
	private void initOwnFields(){
		BigInteger intervall = IdMath.calculateFieldSize(gameState.getMyPlayerMin(), gameState.getMyPlayerMax(), I);

		// Schiffe setzen und Board initialisieren
		ID actualPosition = gameState.getMyPlayerMin();
		ID lastPosition = gameState.getMyPlayerMax();
		
		
		//init all fields with zero
		while(IdMath.idCompare(actualPosition, lastPosition) < 0){
			gameState.setState(actualPosition, FieldState.WATER);
			actualPosition = IdMath.addToID(actualPosition, intervall);
		}
		
		strategy.setShips(gameState, intervall);
	}

	/**
	 * Check if we are beginner
	 * @return
	 */
	private boolean isBeginner() {
		BigInteger maxID = new BigInteger("2").pow(160)
				.subtract(BigInteger.ONE);
		return ID.valueOf(maxID).isInInterval(
				ID.valueOf(chord.getPredecessorID().toBigInteger()
						.add(BigInteger.ONE)), chord.getID());
	}

	/**
	 * Run if all player joined
	 * starts the game
	 */
	private void start() {
		gameState = new GameState(IdMath.addOneToID(chord.getPredecessorID()), chord.getID(), I, S);
		initOwnFields();
		//add own predecessor 
		gameState.addPlayerIfNotExists(chord.getPredecessorID());
		//add player from fingerTable
		ChordImpl c = (ChordImpl) chord;
		List<Node> fTable = c.getFingerTable();
		for (Node n : fTable) {
			gameState.addPlayerIfNotExists(n.getNodeID());
		}
		
		// check beginner
		if (this.isBeginner()) {
			System.out.println("Node is the first Player!");
			attack();
		} else {
			System.out.println("Waiting for the first Player!");
		}

	}

	/**
	 * Create new game
	 * @param args[0] create
	 * @param args[1] ownIP:Port
	 * 
	 * Join existing game
	 * @param args[0] join
	 * @param args[1] ownIP:Port
	 * @param args[2] opponentIP:Port
	 */
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

	/**
	 * Fire on opponents field
	 */
	private void attack() {
		ID target = strategy.findNextTarget(gameState, chord);
		System.out.println("Firing on " + target);
		try {
			chord.retrieve(target);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executed if someone was attacked and resulting broadcast is received
	 * Updated game state and adds source player if unknown 
	 * Recognizes game over
	 * @param source
	 * @param target
	 * @param hit
	 */
	public void updateInformation(ID source, ID target, Boolean hit) {
		gameState.addPlayerIfNotExists(source);
		System.out.println("Updating "+ hit +" shot to " + target);
		gameState.setState(target, hit ? FieldState.HIT : FieldState.WATER);
		
		if (checkGameOver()) {
			System.out.println("Game is Over: Last shot killed a Player");
			try {
				chord.leave();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Check if game is over
	 * @return
	 */
	private boolean checkGameOver() {
		return !gameState.findDeadPlayer().isEmpty();
	}
	
	/**
	 * check if target id is in range of our ship
	 * @param target
	 */
	public void checkHit(ID target) {
		boolean hit = gameState.checkForShip(target);
		gameState.setState(target, hit ? FieldState.HIT : FieldState.WATER);
		System.out.println("Checking if ship was hit with " + target + " Result: " + hit);
		// Broadcast result
		chord.broadcast(target, hit);
		attack();
	}

	// create localhost:1500
	// join localhost:1500 localhost:1501
	/**
	 * Create new game
	 * @param args[0] create
	 * @param args[1] ownIP:Port
	 * 
	 * Join existing game
	 * @param args[0] join
	 * @param args[1] ownIP:Port
	 * @param args[2] opponentIP:Port
	 */
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
