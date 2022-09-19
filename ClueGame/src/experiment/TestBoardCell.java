//Authors: Alex Yen and Grant Renwick
package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
	private int row, col;
	private Boolean isRoom = false, isOccupied = false;
	private Set<TestBoardCell> targets = new HashSet<TestBoardCell>();
	private Set<TestBoardCell> adjList = new HashSet<TestBoardCell>();

	//constructor
	public TestBoardCell(int rows, int columns) {
		setRow(rows);
		setCol(columns);
	}
	//setter to add a cell to this cells adjacency list
	public void addAdjacency(TestBoardCell cell, TestBoard board) {
		//if the cell to the left of the target cell is a valid cell, add it to the adjacency list
		if ((row - 1) >= 0) {
			cell.adjList.add(board.getCell(row - 1, col));
		} 
		//if the cell above the target cell is a valid cell, add it to the adjacency list
		if ((col - 1) >= 0) {
			cell.adjList.add(board.getCell(row, col - 1));
		} 
		//if the cell to the right of the target cell is a valid cell, add it to the adjacency list
		if ((row + 1) < board.ROWS) {
			cell.adjList.add(board.getCell(row + 1, col));
		} 
		//if the cell below the target cell is a valid cell, add it to the adjacency list
		if ((col + 1) < board.COLS) {
			cell.adjList.add(board.getCell(row, col + 1));
		}
	}
	@Override
	public String toString() {
		return "TestBoardCell [row=" + row + ", col=" + col + "]";
	}
	//returns the adjacency list for the cell
	public Set<TestBoardCell> getAdjList() {	//Currently stub
		return adjList;
	}
	//setter indicating a cell is part of a room
	public void setRoom(boolean check) {
		if(check)
		isRoom = true;
		else {
			isRoom = false;
		}
	}
	//getter indicating a cell is part of a room
	public boolean isRoom() {
		return isRoom;
	}
	//setter for indicating a cell is occupied by another player
	public void setOccupied(boolean check) {
		isOccupied = true;
	}
	//getter indicating a cell is occupied by another player
	public boolean getOccupied() {
		return isOccupied;
		
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}

	
}
