package de.haw.battleship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public class RandomStrategy implements Strategy{

	@Override
	public ID findNextTarget(Map<ID, BoardState> enemyBoards, Chord chord) {
		// calculate Range
		
		List<ID> nodeIds = new ArrayList<ID>(enemyBoards.keySet());
		nodeIds.add(chord.getID());
		Collections.sort(nodeIds);
		int preI = nodeIds.indexOf(chord.getPredecessorID());
		ID pre = chord.getPredecessorID();
		ID pre2 = nodeIds.get(preI-1);
		
				BigInteger local = chord.getPredecessorID().toBigInteger();
				BigInteger rangeStart = chord.getID().toBigInteger();
				if (rangeStart == null) {
					rangeStart = BigInteger.ZERO;
				}
				rangeStart.add(BigInteger.ONE);
				BigInteger intervalSize = local.subtract(rangeStart).divide(
						new BigInteger(String.valueOf(100)));
		

		return new ID(chord.getPredecessorID().toBigInteger().subtract(intervalSize).subtract(intervalSize).toByteArray());
	}

}
