package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class KnownCardsPanel extends JPanel{
	
	//Variables for the side panel in the game control panel
	JPanel cardsPanel;
	JPanel peopleCards;
	JPanel roomCards;
	JPanel weaponCards;
	
	public KnownCardsPanel() {
		updateSidePanel();
	}
	public void updateSidePanel() {
		this.removeAll();
		//Store each type of card into arrays
		ArrayList<Card> seenPlayers = new ArrayList<Card>();
		ArrayList<Card> seenWeapons = new ArrayList<Card>();
		ArrayList<Card> seenRooms = new ArrayList<Card>();
		ArrayList<Card> inHandPlayers = new ArrayList<Card>();
		ArrayList<Card> inHandWeapons = new ArrayList<Card>();
		ArrayList<Card> inHandRooms = new ArrayList<Card>();
		
		for (Card inHand : Board.getInstance().getHumanPlayer().getHand()) {
			if (inHand.getCardType() == CardType.PERSON) {
				inHandPlayers.add(inHand);
			} else if (inHand.getCardType() == CardType.WEAPON) {
				inHandWeapons.add(inHand);
			} else if (inHand.getCardType() == CardType.ROOM) {
				inHandRooms.add(inHand);
			}
		}
		
		for (Card seenCard : Board.getInstance().getHumanPlayer().getRevealedCards()) {
			if ((seenCard.getCardType() == CardType.PERSON) && (!inHandPlayers.contains(seenCard))) {
				seenPlayers.add(seenCard);
			} else if ((seenCard.getCardType() == CardType.WEAPON) && (!inHandWeapons.contains(seenCard))) {
				seenWeapons.add(seenCard);
			} else if ((seenCard.getCardType() == CardType.ROOM) && (!inHandRooms.contains(seenCard))) {
				seenRooms.add(seenCard);
			}
		}
		//create labels for the seen and in hand labels
		JLabel seen = new JLabel();
		seen.setText("Seen:");
		JLabel inHand = new JLabel();
		inHand.setText("In Hand:");
		JLabel seen2 = new JLabel();
		seen2.setText("Seen:");
		JLabel inHand2 = new JLabel();
		inHand2.setText("In Hand:");
		JLabel seen3 = new JLabel();
		seen3.setText("Seen:");
		JLabel inHand3 = new JLabel();
		inHand3.setText("In Hand:");
		cardsPanel = new JPanel();
		cardsPanel.setLayout(new GridLayout(3,1)); //has 3 rows 
		cardsPanel.setBorder(BorderFactory.createTitledBorder("Known Cards"));
		//create panel for the seen people
		peopleCards = new JPanel();
		peopleCards.setLayout(new GridLayout(0,1));
		peopleCards.setBorder(BorderFactory.createTitledBorder("People"));
		peopleCards.add(inHand);
		//create panel for seen rooms
		roomCards = new JPanel();
		roomCards.setLayout(new GridLayout(0,1));
		roomCards.setBorder(BorderFactory.createTitledBorder("Rooms"));
		roomCards.add(inHand2);
		//create panel for seen weapons
		weaponCards = new JPanel();
		weaponCards.setLayout(new GridLayout(0,1));
		weaponCards.setBorder(BorderFactory.createTitledBorder("Weapons"));
		weaponCards.add(inHand3);
		
		//add cards to players panel
		//if no cards are in hand, set the text field for in hand players to none
		if (inHandPlayers.isEmpty()) {
			JTextField tempField = new JTextField(15);
			tempField.setEditable(false);
			tempField.setText("None");
			peopleCards.add(tempField);
		} else {
			//if not empty, iterate through each player and add it to a new text field
			for (Card card : inHandPlayers) {
				JTextField tempField = new JTextField(15);
				tempField.setEditable(false);
				tempField.setText(card.getCardName());
				tempField.setBackground(null);
				peopleCards.add(tempField);
			}
		}
		//add seen label
		peopleCards.add(seen);
		//if no cards have been seen from other players, set the text field for seen players to none
		if (seenPlayers.isEmpty()) {
			JTextField tempField = new JTextField(15);
			tempField.setEditable(false);
			tempField.setText("None");
			peopleCards.add(tempField);
		} else {
			//if not empty, iterate through each player and add it a to new text field
			for (Card card : seenPlayers) {
				JTextField tempField = new JTextField(15);
				tempField.setEditable(false);
				tempField.setText(card.getCardName());
				//get color of player who revealed card and set it as background
				tempField.setBackground(Board.getInstance().getHumanPlayer().getPlayerWhoRevealedCard(card).getColor());
				peopleCards.add(tempField);
			}	
		}
		//add cards to rooms panel
		//if no cards are in hand, set the text field for in hand rooms to none
		if (inHandRooms.isEmpty()) {
			JTextField tempField2 = new JTextField(15);
			tempField2.setEditable(false);
			tempField2.setText("None");
			roomCards.add(tempField2);
		} else {
			//if not empty, iterate through each room and add it a new text field
			for (Card card : inHandRooms) {
				JTextField tempField2 = new JTextField(15);
				tempField2.setEditable(false);
				tempField2.setText(card.getCardName());
				tempField2.setBackground(null);
				roomCards.add(tempField2);
			}
		}
		//add seen label
		roomCards.add(seen2);
		//if no cards have been seen from other players, set the text field for seen rooms to none
		if (seenRooms.isEmpty()) {
			JTextField tempField2 = new JTextField(15);
			tempField2.setEditable(false);
			tempField2.setText("None");
			roomCards.add(tempField2);
		} else {
			//if not empty, iterate through each room and add it a to new text field
			for (Card card : seenRooms) {				JTextField tempField2 = new JTextField(15);
				tempField2.setEditable(false);
				tempField2.setText(card.getCardName());
				//get color of player who revealed card and set it as background
				tempField2.setBackground(Board.getInstance().getHumanPlayer().getPlayerWhoRevealedCard(card).getColor());
				roomCards.add(tempField2);
			}
		}
		//add cards to weapons panel
		//if no cards are in hand, set the text field for in hand weapons to none
		if (inHandWeapons.isEmpty()) {
			JTextField tempField3 = new JTextField(15);
			tempField3.setEditable(false);
			tempField3.setText("None");
			weaponCards.add(tempField3);
		} else {
			//if not empty, iterate through each player and add it to a new text field
			for (Card card : inHandWeapons) {
				JTextField tempField3 = new JTextField(15);
				tempField3.setEditable(false);
				tempField3.setText(card.getCardName());
				tempField3.setBackground(null);
				weaponCards.add(tempField3);
			}
		}
		//add seen label
		weaponCards.add(seen3);
		//if no cards have been seen from other players, set the text field for seen weapons to none
		if (seenWeapons.isEmpty()) {
			JTextField tempField3 = new JTextField(15);
			tempField3.setEditable(false);
			tempField3.setText("None");
			weaponCards.add(tempField3);
		} else {
			//if no cards have been seen from other players, set the text field for seen players to none
			for (Card card : seenWeapons) {
				JTextField tempField3 = new JTextField(15);
				tempField3.setEditable(false);
				tempField3.setText(card.getCardName());
				//get color of player who revealed card and set it as background
				tempField3.setBackground(Board.getInstance().getHumanPlayer().getPlayerWhoRevealedCard(card).getColor());
				weaponCards.add(tempField3);
			}
		}
		cardsPanel.add(peopleCards);
		cardsPanel.add(roomCards);
		cardsPanel.add(weaponCards);	
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new GridLayout(0,1));
		sidePanel.add(cardsPanel);
		this.setLayout(new GridLayout(0,1));
		this.add(sidePanel);
	}
	//main for tests
	public static void main(String[] args) {
		Board board;
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load config files 
		board.initialize();
		KnownCardsPanel panel = new KnownCardsPanel();  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(180, 700);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible
		//populate the human player's hand with every card that they didn't draw
		Scanner myObj = new Scanner(System.in);
		String userName = myObj.nextLine();

		for (Player player : Board.getInstance().getPlayers()) {
			if (!player.getName().equals("Solaire of Astora")) {
				for (Card card : player.getHand()) {
					Board.getInstance().getHumanPlayer().addRevealedCard(card);
					Board.getInstance().getHumanPlayer().addCardAndRevealer(card, player);
				}
			}
		}
		for (Player player : Board.getInstance().getPlayers()) {
			if (!player.getName().equals("Solaire of Astora")) {
				for (Card card : player.getHand()) {
					Board.getInstance().getHumanPlayer().addRevealedCard(card);
					Board.getInstance().getHumanPlayer().addCardAndRevealer(card, player);
				}
			}
		}
		panel.updateSidePanel();
		frame.setVisible(false); // make it visible
		frame.setVisible(true); // make it visible
	}
}
