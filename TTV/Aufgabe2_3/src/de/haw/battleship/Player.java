package de.haw.battleship;

import de.uniba.wiai.lspi.chord.data.ID;

public class Player {
	
	private ID max;
	
	private ID min;
	
	public Player(ID max, ID min){
		this.max = max;
		this.min = min;
	}

	public void setMaxID(ID max){
		this.max = max;
	}
	
	public void setMinID(ID min){
		this.min = min;
	}
	
	public ID getMaxID(){
		return max;
	}
	
	public ID getMinID(){
		return min;
	}
	
}
