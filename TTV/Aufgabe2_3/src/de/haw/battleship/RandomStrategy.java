package de.haw.battleship;

import java.util.Map;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public class RandomStrategy implements Strategy{

	@Override
	public ID findNextTarget(Map<ID, BoardState> enemyBoards, Chord chord) {
		// TODO
		return chord.getPredecessorID();
	}

}
