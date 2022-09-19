//Authors: Alex Yen and Grant Renwick
package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {
	private TestBoardCell[][] grid;
	public Set<TestBoardCell> targets = new HashSet<TestBoardCell>();
	public Set<TestBoardCell> visited = new HashSet<TestBoardCell>();

	public final static int COLS = 4;	//can be public since this is a constant
	public final static int ROWS = 4; 	//can be public since this is a constant
	
	//constructor that sets up board
	public TestBoard() {	//Currently stub
		grid = new TestBoardCell[ROWS][COLS];
		//fill the board with new cells
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				grid[i][j] = new TestBoardCell(i,j);
			}
		}
		//calculate all adjacencies for every cell
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				TestBoardCell cell = grid[i][j];
				cell.addAdjacency(cell, this);;
			}
		}
	}
	
	//method that calculates legal targets for a move from startCell of length pathLength

	public void calcTargets(TestBoardCell startCell, int pathLength) {	
		if(visited.contains(startCell)) {
			return;
		}
		if (pathLength == 0 || startCell.isRoom()) {
			targets.add(startCell);
		} else {
			Set<TestBoardCell> adjacencies = startCell.getAdjList();
			visited.add(startCell);
			for (TestBoardCell cell : adjacencies) {
				if(cell.getOccupied() != true)
					calcTargets(cell, pathLength - 1);
			}
		}
		visited.remove(startCell);
	}
	
	//gets targets last created by calcTargets()
	public Set<TestBoardCell> getTargets() {	//Currently stub;
		return targets;
	}
	
	//returns the cell at a specified row and column
	public TestBoardCell getCell(int row, int column) {		//Currently stub
		return grid[row][column];
	}
	
}
