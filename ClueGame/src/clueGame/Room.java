package clueGame;

public class Room {
	private String name;
	private BoardCell centerCell;
	private BoardCell labelCell;
	
	//constructor
	public Room(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BoardCell getLabelCell() {
		return labelCell;
	}

	public BoardCell getCenterCell() {
		return centerCell;
	}
	public void setLabelCell(BoardCell label) {
		labelCell = label;
	}

	public void setCenterCell(BoardCell center) {
		centerCell = center;
	}



}
