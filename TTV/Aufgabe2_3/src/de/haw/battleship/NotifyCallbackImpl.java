package de.haw.battleship;

import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

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
