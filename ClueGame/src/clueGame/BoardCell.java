package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextField;

import experiment.TestBoard;
import experiment.TestBoardCell;

public class BoardCell extends JPanel{
	private int row, col;
	private String initial;
	private DoorDirection doorDirection;
	private Boolean roomLabel, roomCenter = false;
	private char secretPassage;
	private Boolean isRoom = false, isOccupied = false;
	private Set<BoardCell> targets = new HashSet<BoardCell>();
	private Set<BoardCell> adjList = new HashSet<BoardCell>();
	private Room roomOfCell;
	private Boolean isSecretPassage = false;

	//constructor
	public BoardCell() {	//default constructor
		setRow(0);
		setCol(0);
		setSymbol("X");
		Room room = new Room("X");
		setRoom(room);
	}
	public BoardCell(int rows, int columns, String roomSymbol, Room roomType) {
		setRow(rows);
		setCol(columns);
		setSymbol(roomSymbol);
		setRoom(roomType);
	}
	//setter to add a cell to this cells adjacency list
	public void addAdjacencyWalkway(BoardCell cell, Board board, HashMap<Character, Room> roomMap, HashMap<BoardCell, ArrayList<BoardCell>> centersMap) {
		//look for possible edges
		//if the cell to the left of the target cell is a valid cell, add it to the adjacency list
		if ((row - 1) >= 0 && (firstCharUp(board) == 'W')) {
			cell.adjList.add(board.getCell(row - 1, col));
		} 
		//if the cell above the target cell is a valid cell, add it to the adjacency list
		if ((col - 1) >= 0 && (firstCharLeft(board) == 'W')) {
			cell.adjList.add(board.getCell(row, col - 1));
		} 
		//if the cell to the right of the target cell is a valid cell, add it to the adjacency list
		if ((row + 1) < board.getNumRows() && (firstCharDown(board) == 'W')) {
			cell.adjList.add(board.getCell(row + 1, col));
		} 
		//if the cell below the target cell is a valid cell, add it to the adjacency list
		if ((col + 1) < board.getNumColumns() && (firstCharRight(board) == 'W')) {
			cell.adjList.add(board.getCell(row, col + 1));
		}
		
		if (this.isDoorway()) {
			if (this.getDoorDirection() == DoorDirection.LEFT) {
				char leftCellName = firstCharLeft(board);
				cell.adjList.add(roomMap.get(leftCellName).getCenterCell());
				centersMap.get(roomMap.get(leftCellName).getCenterCell()).add(cell);
			}
			if (this.getDoorDirection() == DoorDirection.UP) {
				char upCellName = firstCharUp(board);
				cell.adjList.add(roomMap.get(upCellName).getCenterCell());
				centersMap.get(roomMap.get(upCellName).getCenterCell()).add(cell);
			}
			if (this.getDoorDirection() == DoorDirection.RIGHT) {
				char rightCellName = firstCharRight(board);
				cell.adjList.add(roomMap.get(rightCellName).getCenterCell());
				centersMap.get(roomMap.get(rightCellName).getCenterCell()).add(cell);

			}
			if (this.getDoorDirection() == DoorDirection.DOWN) {
				char downCellName = firstCharDown(board);
				cell.adjList.add(roomMap.get(downCellName).getCenterCell());
				centersMap.get(roomMap.get(downCellName).getCenterCell()).add(cell);
			}
		}
	}
	private char firstCharUp(Board board) {
		return board.getCell(row - 1, col).getInitial().charAt(0);
	}
	private char firstCharLeft(Board board) {
		return board.getCell(row, col - 1).getInitial().charAt(0);
	}
	private char firstCharDown(Board board) {
		return board.getCell(row + 1, col).getInitial().charAt(0);
	}
	
	private char firstCharRight(Board board) {
		return board.getCell(row, col + 1).getInitial().charAt(0);
	}
	
	public void addAdjacencyCenter(ArrayList<BoardCell> entryPoints) {
		for (BoardCell entry : entryPoints) {
			this.adjList.add(entry);
		}
	}
	@Override
	public String toString() {
		return "TestBoardCell [row=" + row + ", col=" + col + "]";
	}
	//returns the adjacency list for the cell
	public Set<BoardCell> getAdjList() {	//Currently stub
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
		if(check)
		isOccupied = true;
		else
			isOccupied = false;
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
	
	public void setSymbol(String symbol) {
		initial = symbol;
	}
	
	public boolean isDoorway() {
		//if a cell has an arrow in it, it is a doorway
		if(initial.contains("<") || initial.contains(">") || initial.contains("v") || initial.contains("^")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setDoorDirection() {
		//if the cell is a doorway, assign its direction based on its arrow
		if(this.isDoorway()) {
			if(initial.contains("<")) {
				doorDirection = DoorDirection.LEFT;
			}
			if(initial.contains(">")){
				doorDirection = DoorDirection.RIGHT;
			}
			if(initial.contains("v")) {
				doorDirection = DoorDirection.DOWN;
			}
			if(initial.contains("^")) {
				doorDirection = DoorDirection.UP;
			}
		}
	}
	
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}
	
	public boolean isLabel() {
		if(initial.contains("#")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isRoomCenter() {
		if(initial.contains("*")) {
			return true;
		} else {
			return false;
		}
	}
	
	public char getSecretPassage() {
		return initial.charAt(1);
	}
	
	public char getSymbol() {
		return initial.charAt(0);
	}
	
	public void setRoom(Room roomType) {
		this.roomOfCell = roomType;
		if (!roomType.getName().equals("Walkway") && !roomType.getName().equals("Unused")) {
			isRoom = true;
		}
	}
	
	public Room getRoom() {
		return roomOfCell;
	}
	
	public String getInitial() {
		return initial;
	}
	
	public void setIsCenterCell() {
		roomCenter = true;
	}
	
	public void setIsLabelCell() {
		roomLabel = true;
	}
	
	public boolean isCenterCell() {
		return roomCenter;
	}
	
	public boolean isLabelCell() {
		return roomLabel;
	}
	
	public void setIsSecretPassage() {
		isSecretPassage = true;
		secretPassage = initial.charAt(1);
	}

	public void drawLabel(int cellHeight, int cellWidth, Graphics g) {
		super.paintComponent(g);
		//set the color of the font
		g.setColor(Color.BLUE);
		//set the font
		g.setFont(new Font("Comic Sans MS", Font.BOLD, cellWidth * 3 / 4));
		//draw the string based on the name
		//if the room name is multiple words, split the string into words and write them below one another
		int tempRow = 0;
		for (String seperatedString : roomOfCell.getName().split(" ")) {
			g.drawString(seperatedString, (getCol() * cellWidth) - getCol()/2,  (getRow() * cellHeight) + tempRow);
			tempRow += cellHeight;
		}

	}
	public void draw(int cellHeight, int cellWidth, Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
		//below fills the board with the correct spaces for colors
		if(getInitial().charAt(0) != 'W' && getInitial().charAt(0) != 'X') {
			g.setColor(Color.GRAY);
			fillBlock(cellHeight, cellWidth, g);
		} else if(getInitial().charAt(0) == 'W'){
			g.setColor(Color.YELLOW);
			fillBlock(cellHeight, cellWidth, g);
			g.setColor(Color.BLACK);
			g.drawRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
		} else {
			g.setColor(Color.BLACK);
			fillBlock(cellHeight, cellWidth, g);
		}

		if (canDrawName()) {
			g.setColor(Color.ORANGE.darker().darker());
			fillBlock(cellHeight, cellWidth, g);
			g.setColor(Color.BLACK);
			g.drawRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
			g.setColor(Color.BLUE);
			//set the font
			g.setFont(new Font("Comic Sans MS", Font.BOLD, cellWidth * 3 / 4));
			g.drawString(initial.substring(1), (getCol() * cellWidth) + (cellWidth / 2) - 3, (getRow() * cellHeight) + cellHeight - 7);
		}

		//below makes the cell cyan if it is a target
		if(Board.getInstance().getCurrentPlayer() instanceof HumanPlayer) {
			HumanPlayer humanPlayer = (HumanPlayer) Board.getInstance().getCurrentPlayer();
			for(BoardCell i : Board.getInstance().getTargets()) {
				if(isValidTarget(i)) {
					g.setColor(Color.CYAN);
					fillBlock(cellHeight, cellWidth, g);
					Rectangle tempRectangle = new Rectangle(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
					humanPlayer.addTargetCell(tempRectangle);
				}
			}
			if(Board.getInstance().getTargets().contains(this)) {
				g.setColor(Color.CYAN);
				fillBlock(cellHeight, cellWidth, g);
				if (!isValidTarget(this)) {
					g.setColor(Color.BLACK);
					g.drawRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
				}
				Rectangle tempRectangle = new Rectangle(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
				humanPlayer.addTargetCell(tempRectangle);
			}
		}

		//below finds out if the space is a door, and if so draws the direction
		if(getInitial().length() > 1) {
			g.setColor(Color.BLUE);
			if(getInitial().charAt(1) == '<') {
				doorLeft(cellHeight, cellWidth, g);
			}
			if(getInitial().charAt(1) == '>') {
				doorRight(cellHeight, cellWidth, g);
			}
			if(getInitial().charAt(1) == 'v') {
				doorDown(cellHeight, cellWidth, g);
			}
			if(getInitial().charAt(1) == '^') {
				doorUp(cellHeight, cellWidth, g);
			}
		}
		
	}
	private void doorUp(int cellHeight, int cellWidth, Graphics g) {
		g.fillRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight / 5);
	}
	private void doorDown(int cellHeight, int cellWidth, Graphics g) {
		g.fillRect(getCol() * cellWidth, getRow() * cellHeight + 22, cellWidth, cellHeight);
	}
	private void doorRight(int cellHeight, int cellWidth, Graphics g) {
		g.fillRect(getCol() * cellWidth + 24, getRow() * cellHeight, cellWidth, cellHeight);
	}
	private void doorLeft(int cellHeight, int cellWidth, Graphics g) {
		g.fillRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth / 5, cellHeight);
	}
	private boolean isValidTarget(BoardCell i) {
		return i.getInitial().charAt(0) == this.getSymbol() && this.getSymbol() != 'W' && !this.isSecretPassage();
	}
	private boolean canDrawName() {
		return this.getInitial().length() == 2 && !this.getInitial().contains("W") && !this.getInitial().contains("*") && !this.getInitial().contains("#");
	}
	private void fillBlock(int cellHeight, int cellWidth, Graphics g) {
		g.fillRect(getCol() * cellWidth, getRow() * cellHeight, cellWidth, cellHeight);
	}
	public boolean isSecretPassage() {
		return isSecretPassage;

	}
}
