package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JPanel;

abstract public class Player extends JPanel{

	private String name;
	private Color color;
	private int row, column;
	private ArrayList<Card> hand;
	private ArrayList<Card> revealedCards;
	private HashMap<Card, Player> cardToRevealerMap = new HashMap<Card, Player>();
	private int roll = 0;
	private ArrayList<Rectangle> targetCells = new ArrayList<Rectangle>();
	private boolean accuseIf = false;
	private boolean movedFromSuggestion = false;
	
	//constructor that sets instance variables
	public Player(String name, Color color, int row, int column) {
		this.hand = new ArrayList<Card>();
		this.revealedCards = new ArrayList<Card>();
		this.color = color;
		this.name = name;
		this.row = row;
		this.column = column;
	}
	public abstract BoardCell selectTarget();
	public void setHand(Card dealtHand) {
		revealedCards.add(dealtHand);
		hand.add(dealtHand);
	}
	public Card disputeSuggestion(Card weapon, Card room, Card person) {
		//create an array list to store any cards that match
		ArrayList<Card> matchingCards = new ArrayList<Card>();
		//iterate through the player's hand who is being suggested
		for (Card card : hand) {
			//if one of the suggested cards is in hand, add it to matching cards
			if (card.equals(weapon) || card.equals(person) || card.equals(room)) {
				matchingCards.add(card);
			}
		}
		//if only one card matched, return that card
		if (matchingCards.size() == 1) {
			return matchingCards.get(0);
		//if multiple cards were matching choose a random card to return
		} else if (matchingCards.size() > 1) {
			Random rand = new Random();
			int randomCardIndex = rand.nextInt(matchingCards.size());
			return matchingCards.get(randomCardIndex);
		}
		//otherwise, return null
		return null;
	}
	public void draw(int cellHeight, int cellWidth, Graphics g) {
		super.paintComponent(g);
		//get the player's color
		if (color == Color.YELLOW.darker()) {
			g.setColor(Color.BLACK);
			g.drawOval(column * cellWidth, row * cellHeight, cellWidth, cellHeight);
		}
		g.setColor(color);
		
		//calculate where to place the player and place them, if in a room, randomly assign placement in room
		Random rand = new Random();
		int randPos = rand.nextInt(20);
		if(Board.getInstance().getCell(this.getRow(), this.getCol()).isRoom() && randPos < 5) {
			g.fillOval(column * cellWidth - randPos, row * cellHeight - randPos, cellWidth, cellHeight);
		} else if(Board.getInstance().getCell(this.getRow(), this.getCol()).isRoom() && randPos < 10) {
			g.fillOval(column * cellWidth + randPos, row * cellHeight - randPos, cellWidth, cellHeight);
		} else if(Board.getInstance().getCell(this.getRow(), this.getCol()).isRoom() && randPos < 15) {
			g.fillOval(column * cellWidth - randPos, row * cellHeight + randPos, cellWidth, cellHeight);
		} else if(Board.getInstance().getCell(this.getRow(), this.getCol()).isRoom() && randPos < 20) {
			g.fillOval(column * cellWidth + randPos, row * cellHeight + randPos, cellWidth, cellHeight);
		} else {
			g.fillOval(column * cellWidth, row * cellHeight, cellWidth, cellHeight);
		}
	}
	public String getName() {
		return name;
	}
	public ArrayList<Card> getHand() {
		return hand;
	}
	public Color getColor() {
		return color;
	}
	public int getRow() {
		return row;
	}
	public int getCol() {
		return column;
	}
	public void setCol(int column) {
		this.column = column;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public void addRevealedCard(Card revealedCard) {
		if (!revealedCards.contains(revealedCard)) {
			revealedCards.add(revealedCard);
		}
	}
	public ArrayList<Card> getRevealedCards() {
		return revealedCards;
	}
	public void addCardAndRevealer(Card card, Player player) {
		cardToRevealerMap.put(card, player);
	}
	public Player getPlayerWhoRevealedCard(Card card) {
		return cardToRevealerMap.get(card);
	}
	public void setRoll(int randRoll) {
		roll = randRoll;
	}
	public int getRoll() {
		return roll;
	}
	public void addTargetCell(Rectangle target) {
		targetCells.add(target);
	}
	public void clearTargetCells() {
		targetCells.clear();
	}
	public ArrayList<Rectangle> getTargetCells() {
		return targetCells;
	}
	public void accuseTime() {
		accuseIf = true;
	}
	public boolean isAccuseIf() {
		return accuseIf;
	}
	public void movingFromSuggestion() {
		movedFromSuggestion = true;
	}
	public boolean getMovedFromSuggestion() {
		return movedFromSuggestion;
	}
	public void noLongerMovedFromSuggestion() {
		movedFromSuggestion = false;
	}
	

}