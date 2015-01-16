package de.haw.battleship;

import java.math.BigInteger;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public interface Strategy {

	public ID findNextTarget(GameState state, Chord chord);
	
	public void setShips(GameState state, BigInteger fieldInterval);
}
