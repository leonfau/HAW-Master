package de.haw.battleship;

public class BoardState {
	
	FieldState[] board;
	
	public BoardState(int size, FieldState initState){
		board = new FieldState[size];
		for(int i = 0; i < size; i++)
			board[i] = initState;
	}
	
	public BoardState(FieldState[] init){
		board = init;
	}
	
	public FieldState[] getState(){
		return board;
	}
	
	public FieldState getPositionState(int position){
		if(position < board.length)
			return board[position];
		else
			return FieldState.UNKNOWN;
	}
	
	public void setShip(int position){
		board[position] = FieldState.SHIP;
	}
	
	public void setHit(int position){
		board[position] = FieldState.HIT;
	}
	
	public void setWater(int position){
		board[position] = FieldState.WATER;
	}
	
	public boolean isHit(int position){
		if(board[position] == FieldState.SHIP){
			board[position] = FieldState.HIT;
			return true;
		}else {
			return false;
		}		
	}
}
