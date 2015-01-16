package de.haw.battleship;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public class RandomStrategy implements Strategy{
	
	private int shipCount;
	private int fieldCount;
	
	public RandomStrategy(int shipCount, int fieldCount) {
		this.shipCount = shipCount;
		this.fieldCount = fieldCount;
	}

	@Override
	public ID findNextTarget(GameState state, Chord chord) {
		List<ID> opponents = state.getOpponents();
		Random ran = new Random();
		//random opponent
		int r = ran.nextInt((opponents.size()-1) + 1);
		ID nextEnemy = opponents.get(r);
		
		//random field
		BigInteger intervall = IdMath.calculateFieldSize(state.getPlayerMinID(nextEnemy), nextEnemy, fieldCount);
		r = ran.nextInt((fieldCount - 1) + 1)+1;
		ID fieldID = IdMath.calcIDforField(state.getMyPlayerMin(), intervall, r);
		while (state.getFieldState(fieldID) != null){            //Überprüfen ob bereits alle felder beschossen wurden --> anderen Spieler beschießen --> prüfen ob wann spiel vorbei ist  
			r = ran.nextInt((fieldCount - 1) + 1)+1;
			fieldID = IdMath.calcIDforField(state.getMyPlayerMin(), intervall, r);
		}
		
		return fieldID;
		

		
/*		
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
		*/
	}
	
	public void setShips(GameState gameState, BigInteger intervall){
		//set ships
				for(int i = 0; i < shipCount; i++){
					Random ran = new Random();
					int r = ran.nextInt((fieldCount - 1) + 1)+1;
					//if there is already an ship on ID try again
					ID shipID = IdMath.calcIDforField(gameState.getMyPlayerMin(), intervall, r);
					while (gameState.getFieldState(shipID) == FieldState.SHIP){
						r = ran.nextInt((fieldCount - 1) + 1)+1;
						shipID = IdMath.calcIDforField(gameState.getMyPlayerMin(), intervall, r);
					}
					gameState.setState(shipID, FieldState.SHIP);		
				}
	}

}
