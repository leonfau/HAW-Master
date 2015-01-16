package de.haw.battleship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniba.wiai.lspi.chord.data.ID;

public class GameState {
	private Map<ID, FieldState> boardState;
	private List<Player> player;
	
	public GameState(){
		boardState = new HashMap<ID, FieldState>();
		player = new ArrayList<Player>();
	}
	
	public void addPlayer(Player player){
		this.player.add(player);
		Player pl = findPlayerForID(player.getMaxID());
		//TODO update old Player if necessary
		//if(pl.)
	}
	
	public Player findPlayerForID(ID id){
		for(Player pl: player){
			if(id.isInInterval(new ID(pl.getMinID().toBigInteger().subtract(BigInteger.ONE).toByteArray()), new ID(pl.getMaxID().toBigInteger().add(BigInteger.ONE).toByteArray())))
				return pl;
		}
		return null;
	}
	
	public void setState(ID id, FieldState state){
		boardState.put(id, state);
	}
	
	public FieldState getFieldState(ID id){
		if(boardState.get(id)!= null){
			return boardState.get(id);
		}
		return FieldState.UNKNOWN;
	}
	

}
