package de.haw.battleship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniba.wiai.lspi.chord.data.ID;

public class GameState {
	/**
	 * Map with all ids and state
	 */
	private Map<ID, FieldState> boardState;
	/**
	 * Map with max id (first) and min id (second) 
	 */
	private Map<ID, ID> player;
	
	private ID myPlayerMin;
	private ID myPlayerMax;
	public GameState(ID myPlayerMin, ID myPlayerMax){
		this.myPlayerMax = myPlayerMax;
		this.myPlayerMin = myPlayerMin;
		boardState = new HashMap<ID, FieldState>();
		player = new HashMap<ID, ID>();
		player.put(myPlayerMax, myPlayerMin);

	}
	
	public ID getMyPlayerMin() {
		return myPlayerMin;
	}

	public void setMyPlayerMin(ID myPlayerMin) {
		this.myPlayerMin = myPlayerMin;
	}

	public ID getMyPlayerMax() {
		return myPlayerMax;
	}

	public void setMyPlayerMax(ID myPlayerMax) {
		this.myPlayerMax = myPlayerMax;
	}

	
	/**
	 * add new player and update min ranges if necessary
	 * @param playerMin
	 * @param playerMax
	 */
	public void addPlayer(ID playerMin, ID playerMax){
		
		//update existing player
		ID[] oldRange = this.findPlayerForIDinRange(playerMax);
		//existing player must be updated
		if(oldRange != null){
			//Max ID of existing player is bigger or equal then new player 
			if(IdMath.isInIntervallInkulsive(playerMax, oldRange[0], oldRange[1])){
				//Min ID of existing node vary
				player.put(oldRange[1], IdMath.addOneToID(playerMax));
				
				//Min ID of existing player is smaller then new player --> min range of new player must be resized
				if(oldRange[1].compareTo(playerMin) <= 0){
					playerMin = oldRange[0];
				}
			}else if(IdMath.isInIntervallInkulsive(playerMin, oldRange[0], oldRange[1])){
				//only min new min range is in existing range --> min range of new player must be resized
				playerMin = IdMath.addOneToID(oldRange[1]);
			}
		}
		this.player.put(playerMax, playerMin);
	}
	
	/**
	 * find possible minID for new player
	 * @param maxID
	 * @return
	 */
	public ID findPossibleMinId(ID maxID){
		ID newMinID = IdMath.zeroID();
		for(Entry<ID, ID> entry : player.entrySet()){
			if(entry.getKey().compareTo(maxID) < 0 && entry.getKey().compareTo(newMinID) > 0){
				newMinID = IdMath.addOneToID(entry.getValue());
			}
		}
		return newMinID;
	}
	
	/**
	 * add player if not exists
	 * @param playerMin
	 * @param playerMax
	 */
	public void addPlayerIfNotExists(ID playerMin, ID playerMax){
		if(this.findPlayerForID(playerMax)== null){
			this.addPlayer(playerMin, playerMax);
		}
	}
	
	/**
	 * find player with given id range
	 * @param id
	 * @return array first entry is min value, second is max value, null if none found
	 */
	public ID[] findPlayerForIDinRange(ID id){
		for(Entry<ID, ID> entry: player.entrySet()){
			if(entry.getKey().compareTo(id) <= 0 && entry.getValue().compareTo(id) >= 0){
				return new ID[] {entry.getValue(), entry.getKey()};
			}
		}
		return null;
	}
	
	
	/**
	 * find player for given max id
	 * @param id
	 * @return
	 */
	public ID findPlayerForID(ID id){
		return player.get(id);
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
