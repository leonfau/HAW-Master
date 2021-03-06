/**
 * Technik & Technologie vernetzter Systeme
 * Teil 2: P2P-Kommunikation: Chord mit Broadcast (3. & 4. Praktikum)
 * Projekt: Implementierung eines verteilten Spiels "Schiffe Versenken" (ohne Churn).
 * 
 * @author Erwin Lang, Leon Fausten
 *
 */
package de.haw.battleship;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

/**
 * 
 * Strategy for the game. Tries to find unknown fields and shoots them randomly.
 */
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
		r = ran.nextInt(fieldCount)+1;
		ID fieldID = IdMath.calcIDforField(state.getPlayerMinID(nextEnemy), intervall, r);
		
		while (state.getFieldState(fieldID) != FieldState.UNKNOWN){            //Überprüfen ob bereits alle felder beschossen wurden --> anderen Spieler beschießen --> prüfen ob wann spiel vorbei ist  
			r = ran.nextInt((fieldCount - 1) + 1)+1;
			fieldID = IdMath.calcIDforField(state.getPlayerMinID(nextEnemy), intervall, r);
		}

		return fieldID;
	}
	@Override
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
					System.out.println("Ship: " +shipID);
					gameState.setState(shipID, FieldState.SHIP);		
				}
	}

}
