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

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public interface Strategy {
	/**
	 * find next ID to attack
	 * @param state
	 * @param chord
	 * @return
	 */
	public ID findNextTarget(GameState state, Chord chord);

	/**
	 * Initialization for own ships
	 * @param state
	 * @param fieldInterval
	 */
	public void setShips(GameState state, BigInteger fieldInterval);
}
