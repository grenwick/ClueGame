package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	private ArrayList<Card> lastSuggestion = new ArrayList<Card>();	//stores the last suggestion so as to make an accusation
	private Set<BoardCell> roomsVisited = new HashSet<BoardCell>();
	
	//constructor that calls parent constructor
	public ComputerPlayer(String name, Color color, int row, int column) {
		super(name, color, row, column);
	}

	@Override
	public BoardCell selectTarget() {
		
		Random rand = new Random();		//random number represents the dice roll that the computer can make
		if (super.getRoll() == 0) {
			int roll = rand.nextInt(6);
			roll += 1;
			setRoll(roll);
		}
		//initializes BoardCell with the location of the computer
		BoardCell location = new BoardCell();
		location.setCol(this.getCol());
		location.setRow(this.getRow());
		BoardCell target = new BoardCell();
		//initializes set that stores all possible targets
		Set<BoardCell> possibleTargets = new HashSet<BoardCell>();
		Board.getInstance().startCalcTargets(Board.getInstance().getCell(getRow(), getCol()), super.getRoll());
		//gets a set of all possible targets from the current place
		possibleTargets = Board.getInstance().getTargets();
		for(BoardCell i : possibleTargets) {
			if(i.isRoom() && !Board.getInstance().getVisited().contains(i) && !roomsVisited.contains(i)) {
				//if i is a room that has not been visited, the target is i, and exit loop
				target = i;
				roomsVisited.add(i);
				break;
			} else if(target != null){
				//below gets a random entry from the possible targets, only used if the target list doesnt have a room
				int randEntry = rand.nextInt(possibleTargets.size());
				int k = 0;
				for(BoardCell j : possibleTargets) {
					if(k == randEntry) {
						target = j;
					}
					k++;
				}
			}
		}
		return target;
	}
	
	public ArrayList<Card> computerSuggestion() {
		lastSuggestion.clear();
		//create arrays for possible players and weapons
		ArrayList<Card> possibleWeapons = new ArrayList<Card>();
		ArrayList<Card> possiblePlayers = new ArrayList<Card>();
		//iterate through all weapons
		for (Card weapon : Board.getInstance().getWeaponCards()) {
			//if weapon has not been seen, it is a possible weapon
			if (!super.getRevealedCards().contains(weapon)) {
				possibleWeapons.add(weapon);
			}
		}
		//iterate though all players
		for (Card p : Board.getInstance().getPlayerCards()) {
			//if player has not been seen, it is a possible player
			if (!super.getRevealedCards().contains(p)) {
				possiblePlayers.add(p);
			}
		}
		//if all players have been seen, just pick the first player
		if (possiblePlayers.size() == 0) {
			possiblePlayers.add(Board.getInstance().getPlayerCards().get(0));
		}
		//if all weapons have been seen, just pick the first weapon
		if (possibleWeapons.size() == 0) {
			possibleWeapons.add(Board.getInstance().getWeaponCards().get(0));
		} 
		//generate a random number and pick a random weapon and player out of possible choices
		Random rand = new Random();
		Card randomWeapon = possibleWeapons.get(rand.nextInt(possibleWeapons.size()));
		Card randomPlayer = possiblePlayers.get(rand.nextInt(possiblePlayers.size()));
		//make a card from the room that the computer is in
		BoardCell currentcell = Board.getInstance().getCell(this.getRow(), this.getCol());
		Room currentRoom = Board.getInstance().getRoom(currentcell);
		Card roomCard = new Card(currentRoom.getName(), CardType.ROOM);
		//add all 3 cards to an arraylist
		ArrayList<Card> suggestedCards = new ArrayList<Card>();
		suggestedCards.add(randomWeapon);
		suggestedCards.add(randomPlayer);
		suggestedCards.add(roomCard);
		lastSuggestion.add(randomWeapon);
		lastSuggestion.add(randomPlayer);
		lastSuggestion.add(roomCard);
		//return the array list
		return suggestedCards;
	}
	public ArrayList<Card> makeAccusation() {	//passes the last suggestion, gameControlPanel checks if the computer should accuse
		return lastSuggestion;
		
	}
	public ArrayList<Card> getLastSuggestion() {
		return lastSuggestion;
	}
	public void moveComputer(BoardCell destinationCell) {
		super.setCol(destinationCell.getCol());
		super.setRow(destinationCell.getRow());
		Board.getInstance().repaint();
	}
	public Set<BoardCell> getRoomsVisited() {
		return roomsVisited;
	}

}
