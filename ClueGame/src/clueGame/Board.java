package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class Board extends JPanel{
	private BoardCell[][] grid;
	private Set<BoardCell> targets = new HashSet<BoardCell>();
	private Set<BoardCell> visited = new HashSet<BoardCell>();
	private String csvFile;
	private String txtFile;
	private ArrayList<String[]> cellData = new ArrayList<String[]>();
	private HashMap<Character, Room> roomMap = new HashMap<Character, Room>();
	private ArrayList<String[]> roomKey = new ArrayList<String[]>();
	private int numColumns;
	private int numRows;
	private HashMap<BoardCell, ArrayList<BoardCell>> centersMap = new HashMap<BoardCell, ArrayList<BoardCell>>();
	private ArrayList<String> weapons = new ArrayList<String>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<String> roomNames = new ArrayList<String>();
	private ArrayList<Card> cards = new ArrayList<Card>();
	private int cardCount;
	private Solution solution = new Solution(null, null, null);
	private ArrayList<Card> weaponCards = new ArrayList<Card>();
	private ArrayList<Card> playerCards = new ArrayList<Card>();
	private ArrayList<Card> roomCards = new ArrayList<Card>();
	private Player mostRecentDisputer;
	private Card mostRecentRevealedCard;
	private Player humanPlayer;
	private Player currentPlayer;
	private int cellWidth, cellHeight;
	private MoveListener moveMouseListener = new MoveListener();
	private Card suggestedPlayer;
	private Card suggestedWeapon;
	private GameControlPanel informationPanel;
	private KnownCardsPanel revealedCardsPanel;
	

	
	
	//constructor that sets up board
    /*
    * variable and methods used for singleton pattern
    */
    private static Board theInstance = new Board();
    // constructor is private to ensure only one can be created
    private Board() {
           super() ;
    }
    // this method returns the only Board
    public static Board getInstance() {
           return getTheInstance();
    }
    /*
     * initialize the board (since we are using singleton pattern)
     */
    public void initialize() {
    	//clear the data structures in case a new board is being initialized
    	cellData = new ArrayList<String[]>();
    	roomMap = new HashMap<Character, Room>();
    	roomKey = new ArrayList<String[]>();
    	targets = new HashSet<BoardCell>();
    	centersMap =  new HashMap<BoardCell, ArrayList<BoardCell>>();

    	players  = new ArrayList<Player>();
    	cards = new ArrayList<Card>();
    	solution = new Solution(null, null, null);
    	weapons = new ArrayList<String>();
    	weaponCards = new ArrayList<Card>();
    	playerCards = new ArrayList<Card>();
    	roomCards = new ArrayList<Card>();
    	cardCount = 0;
		addMouseListener(moveMouseListener);
    	


    	//load the setup and layout for the board
    	try {
			loadSetupConfig();
		} catch (BadConfigFormatException e1) {
			System.out.println(e1.getMessage());
		}
    	try {
			loadLayoutConfig();
		} catch (BadConfigFormatException e) {
			System.out.println(e.getMessage());
		}

    	//create a grid based on the number of rows and columns
		grid = new BoardCell[numRows][numColumns];
		//iterate through room key to make a map of the characters and their room
		generateRooms();
		//fill the board with new cells
		generateBoardCells();
		//calculate all adjacencies for walkways and doorways
		generateWalkwayAdjacencies();
		//calculate adacencies for room centers
		generateRoomCenterAdjacencies();
		for (Player player : players) {
			grid[player.getRow()][player.getCol()].setOccupied(true);
		}
		//makes a solution to the game
		if (players.size() != 0) {
			makeSolution();
			dealHand(players);
		}
		//set the first player
		if (players.size() != 0) {
			currentPlayer = players.get(0);
		}
		
    }
	private void generateRoomCenterAdjacencies() {
		//iterate through all room centers and add their adjacencies
		for (BoardCell center : centersMap.keySet()) {
			center.addAdjacencyCenter(centersMap.get(center));
		}
	}
	private void generateWalkwayAdjacencies() {
		//iterate through the whole board
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j < numColumns; ++j) {
				//get the current cell
				BoardCell cell = grid[i][j];
				//get the initial of the cell
				char roomInitial = cell.getInitial().charAt(0);
				//if the room is a walkway, calculate the walkway's adjacency
				if (roomInitial == 'W') {
					cell.addAdjacencyWalkway(cell, this, roomMap, centersMap);
				}
				//if the cell isn't a walkway, or a center or label, but has length of two, it is a secret passage
				//calculate the adjacency for secretpassages
				if (cell.getInitial().length() == 2 && !cell.getInitial().contains("W") && !cell.getInitial().contains("*") && !cell.getInitial().contains("#")) {
					char secretPassageRoomInitial = cell.getInitial().charAt(1);
					centersMap.get(roomMap.get(roomInitial).getCenterCell()).add(roomMap.get(secretPassageRoomInitial).getCenterCell());
				}
			}
		}
	}
	private void generateRooms() {
		//iterate through the rooms from the setup file
		for (int i = 0; i < roomKey.size(); ++i) {
			//get the symbol for the room and make a new room
			String roomSymbol = roomKey.get(i)[1].substring(1);
			Room tempRoom = new Room(roomSymbol);
			char roomCharacter = roomKey.get(i)[0].charAt(1);
			//put the character and its corresponding room into a map
			roomMap.put(roomCharacter, tempRoom);
		}
	}
	private void generateBoardCells() {
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j < numColumns; ++j) {
				String currentCellName = cellData.get(i)[j];
				grid[i][j] = new BoardCell(i, j, currentCellName, roomMap.get(currentCellName.charAt(0)));
				//if the cell is a door, set its direction
				if (grid[i][j].isDoorway()) {
					grid[i][j].setDoorDirection();
				}
				//if this cell contains a '*' it is a center, so set it as one
				if (currentCellName.contains("*")) {
					roomMap.get(currentCellName.charAt(0)).setCenterCell(grid[i][j]);
					grid[i][j].setIsCenterCell();
					centersMap.put(grid[i][j], new ArrayList<BoardCell>());
				//if this cell contains a '#' it is a label, so set it as one
				} else if (currentCellName.contains("#")) {
					roomMap.get(currentCellName.charAt(0)).setLabelCell(grid[i][j]);
					grid[i][j].setIsLabelCell();
				}
				//set secret passage
				if (currentCellName.length() == 2 && !currentCellName.contains("W") && !currentCellName.contains("*") && !currentCellName.contains("#")) {
					grid[i][j].setIsSecretPassage();
				}
			}
		}
	
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//get the width and height of each cell
		cellWidth = this.getWidth() / numColumns;
		cellHeight= this.getHeight() / numRows;
		//iterate through the board and draw each cell
		for (int i = 0; i < grid.length; ++i) {
			for (int j = 0; j < grid[i].length; ++j) {
				grid[i][j].draw(cellHeight, cellWidth, g);
			}
		}
		
		//iterate through each cell and if it's a label, draw the label
		for (int i = 0; i < grid.length; ++i) {
			for (int j = 0; j < grid[i].length; ++j) {
				if (grid[i][j].isLabel()) {
					grid[i][j].drawLabel(cellHeight, cellWidth, g);
				}
			}
		}
		//iterate through each player and draw them
		for (Player drawPlayer : players) {
			drawPlayer.draw(cellHeight, cellWidth, g);
		}	
	}
	
	private class MoveListener implements MouseListener {
		@Override
		public void mousePressed (MouseEvent e) {
			//get human player
			HumanPlayer humanPlayer = (HumanPlayer) players.get(0);
			//if it is not the player's turn, do nothing
			if (humanPlayer.isTurnOver()) {
				return;
			} else {
				//get all possible movement positions for the human
				for (Rectangle possibleRect : humanPlayer.getTargetCells()) {
					//if the cursor was in the rectangle, move the player
					if (possibleRect.contains(e.getX(),e.getY())) {
						int x = (int)(possibleRect.getY() / possibleRect.getHeight());
						int y = (int) (possibleRect.getX() / possibleRect.getWidth());
						grid[humanPlayer.getRow()][humanPlayer.getCol()].setOccupied(false);
						grid[x][y].setOccupied(true);
						//see if the current cell is a room
						if (grid[x][y].isRoom()) {
							//put player in room and finish their turn
							String roomName = grid[x][y].getInitial();
							Room selectedRoom = roomMap.get(roomName.charAt(0));
							BoardCell targetCell = selectedRoom.getCenterCell();
							humanPlayer.selectTarget(targetCell.getCol(), targetCell.getRow());
							humanPlayer.turnFinished();
							//repaint and clear the possible target cells
							Board.getInstance().repaint();
							Board.getInstance().getTargets().clear();
							humanPlayer.clearTargetCells();
							SuggestionDialog playerSuggestion = new SuggestionDialog(grid[x][y].getRoom());
							playerSuggestion.setModal(true);
							playerSuggestion.setVisible(true);
							
							return;
						} else {
							//if the player didn't select a room, just move them to the board cell where the cursor is
							humanPlayer.selectTarget(y, x);
							humanPlayer.turnFinished();
							Board.getInstance().repaint();
							Board.getInstance().getTargets().clear();
							humanPlayer.clearTargetCells();
							return;
						}
						
					} 
				}
				//if the cursor was not in one of the target cells, give an error message
				JOptionPane.showMessageDialog(null, "That is not a target.");
				return;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

	public void makeSolution() {	//randomly selects a room, person, and weapon to be the solution to the game
		Random rand = new Random();
		int randRoom = rand.nextInt(9);
		Card randRoomCard = cards.get(randRoom);		//stores in temp to be added to solution later
		int randPerson = rand.nextInt(6);
		Card randPersonCard = cards.get(randPerson + 9);	//stores in temp to be added to solution later
		int randWeapon = rand.nextInt(6);
		Card randWeaponCard = cards.get(randWeapon + 15);	//stores in temp to be added to solution later
		solution = new Solution(randRoomCard, randPersonCard, randWeaponCard);	//adds new set of cards to solution
		//removes the cards that were selected for the solution from the deck
		cards.remove(randRoom);
		cards.remove(8 + randPerson);
		cards.remove(13 + randWeapon);
	}
	
	public void dealHand(ArrayList<Player> list) {	//randomly selects 3 cards from deck and deals them to the player
		Random rand = new Random();
		int deckSize = 18;
		for(Player i : players) {
			for(int j = 0; j < 3; j++) {
				int randNum = rand.nextInt(deckSize);
				Card randCard = cards.get(randNum);
				i.setHand(randCard);
				cards.remove(randNum);				//remove card once it has been given to a character
				deckSize--;
			}
		}
	}
    
    public void startCalcTargets(BoardCell startCell, int pathLength) {
    	//clear possible targets
    	targets.clear();
    	if (currentPlayer.getMovedFromSuggestion()) {
    		targets.add(startCell);
    	}
    	currentPlayer.noLongerMovedFromSuggestion();
    	//if the cell is a room set it to false so the recursive function doens't instantly end
    	if (startCell.isRoom()) {
    		startCell.setRoom(false);
    		calcTargets(startCell, pathLength);
    		startCell.setRoom(true);
    	} else {
    		//otherwise it is a walkway, calculate the targets normally
    		calcTargets(startCell, pathLength);
    	}
    }
    
    //method that checks the solution from an accusation
    public boolean handleAccusation(Card room, Card weapon, Card player) {
    	if (solution.getPerson().equals(player) && solution.getRoom().equals(room) && solution.getWeapon().equals(weapon)) {
    		return true;
    	}
    	return false;
    }

	//method that calculates legal targets for a move from startCell of length pathLength
	public void calcTargets(BoardCell startCell, int pathLength) {	
		//if the cell has been visited don't go to it again
		if(visited.contains(startCell)) {
			return;
		}
		//if the pathLength is 0 or a room can be reached, store it as a target
		if (pathLength == 0 || startCell.isRoom()) {
			targets.add(startCell);
		} else {
			//iterate through this cell's possible adjacencies and recursively check them while adding them to visited
			Set<BoardCell> adjacencies = startCell.getAdjList();
			visited.add(startCell);
			for (BoardCell cell : adjacencies) {
				if(cell.getOccupied() != true || (cell.getOccupied() == true && cell.isRoom() == true))
					calcTargets(cell, pathLength - 1);
			}
		}
		//remove this cell from visited so it can be checked by another path that hasn't visited it
		visited.remove(startCell);
	}
	
	public boolean checkAccusation(Card weapon, Card room, Card player){
		//if the solution's cards are the same as the accused, return true
		if (solution.getPerson().equals(player) && solution.getRoom().equals(room) && solution.getWeapon().equals(weapon)) {
			return true;
		}
		return false;
	}
	public Card handleSuggestion(Player suggestor, Room suggestionRoom, Card suggestedWeapon, Card suggestedPlayer) {
		//create a room card from the current room
		Card roomCard = new Card(suggestionRoom.getName(), CardType.ROOM);
		ArrayList<Player> orderedPlayers = new ArrayList<Player>();
		int playerIndex = 0;
		//order the players from where the current suggestor is to form, basically shift the circle
		for (int i = 0; i < players.size(); ++i) {
			if (players.get(i).equals(suggestor)) {
				playerIndex = i;
				break;
			}
		}
		//push the starting player to the end
		playerIndex += 1;
		//order the players
		for (int i = 0; i < players.size() - 1; ++i) {
			if (playerIndex > players.size() - 1) {
				playerIndex = 0;
			}
			orderedPlayers.add(players.get(playerIndex));
			playerIndex++;
		}
		//see if a player from all players can dispute the suggestion, go in order
		for (Player askedPlayer : orderedPlayers) {
			Card suggestionResult = askedPlayer.disputeSuggestion(suggestedWeapon, roomCard, suggestedPlayer);
			if (suggestionResult != null) {
				suggestor.addRevealedCard(suggestionResult);
				mostRecentRevealedCard = suggestionResult;
				mostRecentDisputer = askedPlayer;
				suggestor.addCardAndRevealer(suggestionResult, askedPlayer);
				if (players.size() == 6) {
					grid[suggestedPlayer.getPlayerFromCard().getRow()][suggestedPlayer.getPlayerFromCard().getCol()].setOccupied(false);
					suggestedPlayer.getPlayerFromCard().setCol(suggestor.getCol());
					suggestedPlayer.getPlayerFromCard().setRow(suggestor.getRow());
					suggestedPlayer.getPlayerFromCard().movingFromSuggestion();
					grid[suggestedPlayer.getPlayerFromCard().getRow()][suggestedPlayer.getPlayerFromCard().getCol()].setOccupied(true);

				}
				return suggestionResult;
			}
		}
		//if no card was found, return null
		mostRecentDisputer = null;
		mostRecentRevealedCard = null;
		suggestor.accuseTime();
		return null;
	}
	
 	public void loadSetupConfig() throws BadConfigFormatException{
		
		try {
			//load the file and iterate through the lines
			FileReader reader = new FileReader(txtFile);
			Scanner in = new Scanner(reader);
			while(in.hasNextLine()) {
				String line = in.nextLine();
				//ignore any comments in file which start with a '/'
				if (line.charAt(0) != '/') {
					//split the row
					String[] splitLine = line.split(",");
					String[] temp = new String[2];
					//check if the first word is either room or if it is space, if it isn't throw an exception
					try {
						if (!splitLine[0].equals("Room") && !splitLine[0].equals("Space") && !splitLine[0].equals("Weapon") && !splitLine[0].equals("Player")) {
							throw new BadConfigFormatException("There is a type of cell that is not a room or space in this setup.");
						} 
					} catch (BadConfigFormatException e) {
						System.out.println(e.getMessage());
						throw e;
					}
					//store the character associated with a room
					if (splitLine[0].equals("Room") || splitLine[0].equals("Space")) {
						temp[0] = splitLine[2];
						//store the room name
						temp[1] = splitLine[1];
						//store the character and name into an arraylist of arrays
						roomKey.add(temp);
						if(splitLine[0].equals("Room")) {
							roomNames.add(splitLine[1].trim());
							Card tempCard = new Card(splitLine[1].trim() , CardType.ROOM);
							cards.add(tempCard);
							roomCards.add(tempCard);
							setCardCount(getCardCount() + 1);
						}
					} else if (splitLine[0].equals("Weapon")){
						//get the weapon name and add it to the arrays of weapons
						weapons.add(splitLine[1].trim());
						//make a card of the weapon
						Card tempCard = new Card(splitLine[1].trim() , CardType.WEAPON);
						cards.add(tempCard);
						weaponCards.add(tempCard);
						setCardCount(getCardCount() + 1);
					} else {
						//If it is not a room, weapon, or space, it is a player
						//get the correct value types of the player information
						int tempRow = Integer.parseInt(splitLine[4].trim());
						int tempColumn = Integer.parseInt(splitLine[5].trim());
						Color tempColor = this.stringToColor(splitLine[2].trim());
						//see if the player is a human or a computer and add it the the array of players
						if (splitLine[3].trim().equals("Human")) {
							HumanPlayer tempPlayer = new HumanPlayer(splitLine[1].trim(), tempColor, tempRow, tempColumn);
							humanPlayer = tempPlayer;
							players.add(tempPlayer);
						} else {
							ComputerPlayer tempPlayer = new ComputerPlayer(splitLine[1].trim(), tempColor, tempRow, tempColumn);
							players.add(tempPlayer);
						}
						Card tempCard = new Card(splitLine[1].trim(), CardType.PERSON);
						cards.add(tempCard);
						playerCards.add(tempCard);
						setCardCount(getCardCount() + 1);
					}
				}
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	public void loadLayoutConfig() throws BadConfigFormatException {
		try {
			//load the file and iterate through it
			FileReader reader = new FileReader(csvFile);
			Scanner in = new Scanner(reader);
			String row;
			String[] rowSplit;
			while(in.hasNextLine()) {
				//read the row and split it by commas
				row = in.nextLine();
				rowSplit = row.split(",");
				try {
					//Check if this space's character is in the legend of possible rooms
					for (int i = 0; i < rowSplit.length; ++i) {
						boolean check = false;
						//loop through each possible room it could be based on legend generated from set up
						for (int j = 0; j < roomKey.size(); ++j) {
							//if the character in the space is in the legend set check to true and don't throw the exception
							boolean isValidRoomCharacter = roomKey.get(j)[0].contains(rowSplit[i].substring(0, 1));
							if (isValidRoomCharacter) {
								check = true;				
							}
						}
						if (!check) {
							throw new BadConfigFormatException("A symbol from this file does not match any of the given symbols loaded in setup.");	
						}
					}
					//add the row to cellData
					cellData.add(rowSplit);
					//if the length of one row doesn't match the length of the first one, the board is not correctly
					//laid out so an error should be thrown
					if (cellData.get(0).length != rowSplit.length) {
						throw new BadConfigFormatException("The columns are of inconsistent length in this file.");
					}
				} catch (BadConfigFormatException e) {
					System.out.println(e.getMessage());
					throw e;
				}

			}
			//store the number of columns and number of rows
			numColumns = cellData.get(0).length;	
			numRows = cellData.size(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//set the color based on the string passed in
	public Color stringToColor(String colorString) {
		switch (colorString.toLowerCase()) {
		case "yellow":
			return  Color.YELLOW.darker();
		case "cyan":
			return Color.CYAN.darker();
		case "orange":
			return Color.ORANGE;
		case "red":
			return Color.RED;
		case "light brown":
			return Color.YELLOW.darker().darker().darker();
		case "gray":
			return Color.LIGHT_GRAY;
		}
		return Color.WHITE;
	}
	//class for the suggestion dialog
	public class SuggestionDialog extends JDialog {
		private JComboBox<String> playersCombo = createPlayersCombo();
		private JComboBox<String> weaponsCombo = createWeaponsCombo();
		public SuggestionDialog(Room currentRoom) {
			setTitle("Make a suggestion");
			setSize(300,300);
			setLayout(new GridLayout(0,2));

			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new GridLayout(4,0));
			JLabel roomLabel = new JLabel("Current room");
			JLabel personLabel = new JLabel("Person");
			JLabel weaponLabel = new JLabel("Weapon");
			JButton submitButton = new JButton("Submit");
			submitButton.addActionListener(new SubmitListener());
			leftPanel.add(roomLabel);
			leftPanel.add(personLabel);
			leftPanel.add(weaponLabel);
			leftPanel.add(submitButton);
			add(leftPanel);
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new GridLayout(4,0));
			JTextField pickedRoom = new JTextField();
			pickedRoom.setText(currentRoom.getName());
			pickedRoom.setEditable(false);
			rightPanel.add(pickedRoom);
			
			rightPanel.add(playersCombo);
			
			rightPanel.add(weaponsCombo);			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CancelListener());
			rightPanel.add(cancelButton);
			add(rightPanel);		
			
		}
		public class CancelListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}
		public class SubmitListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				//get the options the player suggested
				String suggestedPlayerName = (String) playersCombo.getSelectedItem();
				String suggestedWeaponName = (String) weaponsCombo.getSelectedItem();
				suggestedPlayer = getPlayerCardFromString(suggestedPlayerName);
				suggestedWeapon = getWeaponCardFromString(suggestedWeaponName);
				//process the suggestion
				Card suggestionResult = handleSuggestion(humanPlayer, grid[humanPlayer.getRow()][humanPlayer.getCol()].getRoom(), suggestedWeapon, suggestedPlayer);
				informationPanel.setGuess(suggestedPlayer.getCardName() + ", " + grid[humanPlayer.getRow()][humanPlayer.getCol()].getRoom().getName() + ", " + suggestedWeapon.getCardName());
				if (suggestionResult == null) {
					informationPanel.setGuessResult("No new clue");
				} else {
					informationPanel.setGuessResult("Suggestion disproven!");
				}
				//update side panel
				revealedCardsPanel.updateSidePanel();
				revealedCardsPanel.setVisible(false);
				revealedCardsPanel.setVisible(true);
				dispose();
			}
			
		}
	}
	public JComboBox<String> createPlayersCombo() {
		//create a combo box and add all players to it
		JComboBox<String> playerList = new JComboBox<String>();
		for (Player player : players) {
			playerList.addItem(player.getName());
		}
		return playerList;
	}
	public JComboBox<String> createWeaponsCombo() {
		//create a combo box and add all weapons to it
		JComboBox<String> weaponList = new JComboBox<String>();
		for (String weapon : weapons) {
			weaponList.addItem(weapon);
		}
		return weaponList;
	}
	public JComboBox<String> createRoomsCombo() {
		//create a combo box and add all weapons to it
		JComboBox<String> roomList = new JComboBox<String>();
		for (String room : roomNames) {
			roomList.addItem(room);
		}
		return roomList;
	}

	//getters and setters below
	public Room getRoom(BoardCell cell) {
		return cell.getRoom();
	}
	
	public void setVisited(Set<BoardCell> visited) {
		this.visited = visited;
	}
	public Set<BoardCell> getVisited() {
		return visited;
	}
	public Room getRoom(char c) {
		return roomMap.get(c);
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public int getNumColumns() {
		return numColumns;
	}
	public ArrayList<String[]> getSpaces() {
		return cellData;
	}
	public Set<BoardCell> getAdjList(int i, int j) {
		return grid[i][j].getAdjList();
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public ArrayList<String> getWeapons() {
		return weapons;
	}
	public ArrayList<Card> getCards() {
		return cards;
	}
	public Solution getSolution() {
		return solution;
	}
	public void setCardCount(int num) {
		cardCount = num;
	}
	public int getCardCount() {
		return cardCount;
	}

	public static Board getTheInstance() {
		return theInstance;
	}

	//gets targets last created by calcTargets()
	public Set<BoardCell> getTargets() {	//Currently stub;
		return targets;
	}
	
	public void setTargets(Set<BoardCell> targets) {
		this.targets = targets;
	}
	//returns the cell at a specified row and column
	public BoardCell getCell(int row, int column) {		//Currently stub
		return grid[row][column];
	}
	
	public void setConfigFiles(String csvName, String txtName) {
		csvFile = "data/" + csvName;
		txtFile = "data/" + txtName;		

	}
	public void clearPlayers() {
		players.clear();
	}
	public void addPlayer(Player addedPlayer) {
		players.add(addedPlayer);
	}
	public ArrayList<Card> getWeaponCards() {
		return weaponCards;
	}
	public ArrayList<Card> getPlayerCards() {
		return playerCards;
	}
	public void clearWeaponCards() {
		weaponCards.clear();
	}
	public void clearPlayerCards() {
		playerCards.clear();
	}
	public void addWeaponCard(Card weapon) {
		weaponCards.add(weapon);
	}
	public void addPlayerCard(Card player) {
		playerCards.add(player);
	}
	public Player getPlayerFromName(String name) {
		for (Player player: players) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	public Player getMostRecentDisputer() {
		return mostRecentDisputer;
	}
	public Card getMostRecentRevealedCard() {
		return mostRecentRevealedCard;
	}
	public Player getHumanPlayer() {
		return humanPlayer;
	}
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	public Player nextPlayer() {
		if (players.size() != 0) {
			int currentPlayerIndex = players.indexOf(currentPlayer);
			currentPlayerIndex = (currentPlayerIndex + 1) % 6;
			currentPlayer = players.get(currentPlayerIndex);
			return currentPlayer;
		}

		return null;
	}

	public int getCellWidth() {
		return cellWidth;
	}
	public int getCellHeight() {
		return cellHeight;
	}
	public BoardCell[][] getGrid() {
		return grid;
	}
	public Card getWeaponCardFromString(String weaponString) {
		for (Card weaponCard : weaponCards) {
			if (weaponCard.getCardName().equals(weaponString)) {
				return weaponCard;
			}
		}
		return null;
	}
	public Card getPlayerCardFromString(String playerString) {
		for (Card playerCard : playerCards) {
			if (playerCard.getCardName().equals(playerString)) {
				return playerCard;
			}
		}
		return null;
	}
	public Card getRoomCardFromString(String roomString) {
		for (Card roomCard : roomCards) {
			if (roomCard.getCardName().equals(roomString)) {
				return roomCard;
			}
		}
		return null;
	}
	public void setGameControlPanel(GameControlPanel setPanel) {
		informationPanel = setPanel;
	}
	public void setRevealedCardsPanel(KnownCardsPanel setPanel) {
		revealedCardsPanel = setPanel;
	}
}

;