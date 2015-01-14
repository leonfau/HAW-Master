package de.haw.battleship;

public class BoardState {
	
	FieldState[] board;
	
	public BoardState(int size){
		board = new FieldState[size];
		for(int i = 0; i < size; i++)
			board[i] = FieldState.WATER;
	}
	
	public BoardState(FieldState[] init){
		board = init;
	}
	
	public FieldState[] getState(){
		return board;
	}
	
	public FieldState getPositionState(int position){
		if(position <= board.length+1)
			return board[position+1];
		else
			return FieldState.UNKNOWN;
	}
	
	public void setShip(int position){
		board[position-1] = FieldState.SHIP;
	}
	
	public FieldState fire(int position){
		if(board[position-1] == FieldState.SHIP){
			board[position-1] = FieldState.HIT;
			return FieldState.HIT;
		}else {
			return FieldState.WATER;
		}		
	}
}
