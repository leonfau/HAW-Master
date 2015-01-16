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
	private BoardState ownBoard;
	private Map<ID, BoardState> enemyBoards;
	Strategy strategy;

	private static final int I = 100;
	private static final int S = 10;
	private BigInteger intervalSize;
	private BigInteger rangeStart;

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
			int r = ran.nextInt(I - 1) + 1;
			if (ownBoard.getPositionState(r) != FieldState.SHIP) {
				ownBoard.setShip(r);
				i++;
			}
		}

		enemyBoards = new HashMap<ID, BoardState>();

		strategy = new RandomStrategy();
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
		// Build Enemymap
		ChordImpl c = (ChordImpl) chord;
		List<Node> fTable = c.getFingerTable();

		for (Node n : fTable) {
			if (!enemyBoards.containsKey(n.getNodeID())) {
				enemyBoards.put(n.getNodeID(), new BoardState(I,
						FieldState.UNKNOWN));
			}
		}

		// calculate Range
		BigInteger local = chord.getID().toBigInteger();
		rangeStart = chord.getPredecessorID().toBigInteger();
		if (rangeStart == null) {
			rangeStart = BigInteger.ZERO;
		}
		rangeStart.add(BigInteger.ONE);
		intervalSize = local.subtract(rangeStart).divide(
				new BigInteger(String.valueOf(I)));

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
		System.out.println("attack");
		ID Target = strategy.findNextTarget(enemyBoards, chord);
		try {
			chord.retrieve(Target);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void updateInformation(ID source, ID target, Boolean hit) {
		if(enemyBoards.get(source) == null){
			enemyBoards.put(source, new BoardState(I, FieldState.UNKNOWN));
		}

		System.out.println("update");
		
		List<ID> nodeIds = new ArrayList<ID>(enemyBoards.keySet());
		nodeIds.add(chord.getID());
		Collections.sort(nodeIds);
		
		ID id1 = new ID(null);
		ID id2 = new ID(null);
		boolean found = false;
		int i = 0;
		while (!found && i == nodeIds.size()-1) {
			id1 = nodeIds.get(i);
			id2 = new ID(nodeIds.get(i+1).toBigInteger().add(BigInteger.ONE).toByteArray());
			
			found = target.isInInterval(id1, id2);
			i++;
		}
		int field = id2.toBigInteger().subtract(id1.toBigInteger()).divide(BigInteger.valueOf(I)).intValue();
		BoardState targetBoard = enemyBoards.get(nodeIds.get(i));
		
		System.out.println(nodeIds.get(0).toDecimalString());
		System.out.println(nodeIds.get(1).toDecimalString());
		System.out.println(field);
		
		if(hit){
			targetBoard.setHit(field);
		}else{
			targetBoard.setWater(field);
		}

		enemyBoards.put(target, targetBoard);
	}

	public void checkHit(ID target) {
		System.out.println("check hit");
		BigInteger field = target.toBigInteger().subtract(rangeStart)
				.divide(intervalSize);
		System.out.println(field);

		// check if Hit
		boolean hit = ownBoard.isHit(field.intValue());
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
