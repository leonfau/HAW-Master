package de.haw.battleship;

import java.util.Map;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public interface Strategy {

	public ID findNextTarget(Map<ID, BoardState> enemyBoards, Chord chord);
}
