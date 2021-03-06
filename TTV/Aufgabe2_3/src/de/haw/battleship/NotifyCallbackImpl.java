/**
 * Technik & Technologie vernetzter Systeme
 * Teil 2: P2P-Kommunikation: Chord mit Broadcast (3. & 4. Praktikum)
 * Projekt: Implementierung eines verteilten Spiels "Schiffe Versenken" (ohne Churn).
 * 
 * @author Erwin Lang, Leon Fausten
 *
 */
package de.haw.battleship;

import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

/**
 * 
 *Interface between OpenChord and the game
 */
public class NotifyCallbackImpl implements NotifyCallback {

	Game game;
	
	public NotifyCallbackImpl(Game game) {
		this.game = game;
	}
	
	@Override
	public void retrieved(ID target) {
		Logger.getGlobal().info(String.format("Retrieve for Target %s", target));
		game.checkHit(target);
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		Logger.getGlobal().info(String.format("Broadcast for attack %s from %s to %s", hit, source, target));
		game.updateInformation(source, target, hit);
	}

}
