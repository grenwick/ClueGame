package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import clueGame.Board.SuggestionDialog;
import clueGame.Board.SuggestionDialog.CancelListener;
import clueGame.Board.SuggestionDialog.SubmitListener;

public class GameControlPanel extends JPanel {
	
	//Variables for the top panel in the game control panel
	JPanel topPanel;
	JPanel turnPanel;
	JPanel rollPanel;
	JLabel rollLabel;
	JLabel turnLabel;
	JTextField currentPlayerField;
	JTextField currentRollField;
	
	//Variables for the bottom panel in the game control panel
	JPanel bottomPanel;
	JPanel guessPanel;
	JPanel guessResultPanel;
	JTextField currentGuessField;
	JTextField currentGuessResultField;
	KnownCardsPanel knownCards;
	
	//variables for debug
	int count = 0;
	

	
	
	/**
	 * Constructor for the panel, it does 90% of the work
	 */
	public GameControlPanel(KnownCardsPanel knownCards)  {
		this.knownCards = knownCards;
		this.setLayout(new GridLayout(2,0));
		//make buttons
		JButton accusationButton = new JButton("Make Accusation");
		JButton nextPlayerButton = new JButton("NEXT!");
		accusationButton.addActionListener(new AccusationListener());
		nextPlayerButton.addActionListener(new NextListener());
		//make a top panel to put labels in
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,4));
		//topPanel.setLayout(new GridLayout(0,0));
		//create panels for turn section and roll section
		turnPanel = new JPanel();
		//turnPanel.setLayout(new GridLayout(1,2));
		rollPanel = new JPanel();
		//create labels for turn section and roll section
		rollLabel = new JLabel("Roll:");
		turnLabel = new JLabel("Whose Turn?");
		
		//create text fields to show the current roll and current player
		currentPlayerField = new JTextField(23);
		currentRollField = new JTextField(5);
		//turn off editing in text fields
		currentPlayerField.setEditable(false);
		currentRollField.setEditable(false);
		//insert turn labels into turn panel
		turnPanel.add(turnLabel);
		turnPanel.add(currentPlayerField);
		//insert roll labels into roll panel
		rollPanel.add(rollLabel);
		rollPanel.add(currentRollField);
		//add sub panels and buttons to top panel
		topPanel.add(turnPanel);
		topPanel.add(rollPanel);
		topPanel.add(accusationButton);
		topPanel.add(nextPlayerButton);
		//add top panel to the game control panel
		this.add(topPanel);
		
		//create a panel for the bottom panel
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(0,2));
		//create a panel for the guess
		guessPanel = new JPanel();
		guessPanel.setLayout(new GridLayout(1,0));
		guessPanel.setBorder(BorderFactory.createTitledBorder("Guess"));
		//create a panel for the guess result
		guessResultPanel = new JPanel();
		guessResultPanel.setLayout(new GridLayout(1,0));
		guessResultPanel.setBorder(BorderFactory.createTitledBorder("Guess Result"));
		//create text fields for the guess and guess result
		currentGuessField = new JTextField();
		currentGuessResultField = new JTextField();
		//make sure text fields cannot be edited by user
		currentGuessField.setEditable(false);
		currentGuessResultField.setEditable(false);
		//add the fields to the panels
		guessPanel.add(currentGuessField);
		guessResultPanel.add(currentGuessResultField);
		//add the bottom panel to the overall panel
		bottomPanel.add(guessPanel);
		bottomPanel.add(guessResultPanel);
		this.add(bottomPanel);
		
	}
	
	public void setTurn(Player currentPlayer, int currentRoll) {
		currentPlayerField.setText(currentPlayer.getName());
		currentPlayerField.setBackground(currentPlayer.getColor());
		currentRollField.setText(Integer.toString(currentRoll));
		currentPlayer.setRoll(currentRoll);
	}
	
	public void setGuess(String guess) {
		currentGuessField.setText(guess);
		currentGuessField.setBackground(currentPlayerField.getBackground());
	}
	public void setGuessResult(String result) {
		currentGuessResultField.setText(result);
		//if the most recent guess was disputed, set the background to the player that disputed it
		if (Board.getInstance().getMostRecentRevealedCard() == null) {
			currentGuessResultField.setBackground(null);
		} else {
			currentGuessResultField.setBackground(Board.getInstance().getMostRecentDisputer().getColor());
		}
		
	}
	public void clearGuess() {
		currentGuessField.setText("");
		currentGuessField.setBackground(null);
	}
	public void clearGuessResult() {
		currentGuessResultField.setText("");
		currentGuessResultField.setBackground(null);
	}

	class NextListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) { 
			//Get the next player and display them
			for (int i = 0; i < Board.getInstance().getGrid().length; ++i) {
				for (int j = 0; j < Board.getInstance().getGrid()[i].length; ++j) {
					Board.getInstance().getGrid()[i][j].setOccupied(false);
				}
			}
			for (Player p : Board.getInstance().getPlayers()) {
				Board.getInstance().getGrid()[p.getRow()][p.getCol()].setOccupied(true);
			}
			clearGuess();
			clearGuessResult();
			if (Board.getInstance().getCurrentPlayer() instanceof HumanPlayer) {
				HumanPlayer currentHumanPlayer = (HumanPlayer) Board.getInstance().getCurrentPlayer();
				//see if the human player's turn is over
				if (currentHumanPlayer.isTurnOver()) {
					//if the turn is over, get and set the next player
					Board.getInstance().nextPlayer();
					Player currentPlayer = Board.getInstance().getCurrentPlayer();
					Random rand1 = new Random();
					int randRoll = rand1.nextInt(6) + 1;
					setTurn(currentPlayer, randRoll);
					moveComputerPlayer();
				//if the turn isn't over, give the player an error message
				} else {
					JOptionPane.showMessageDialog(null, "Please finish your turn!");
				}
			} else {
				//go to the next player
				Board.getInstance().nextPlayer();
				Player currentPlayer = Board.getInstance().getCurrentPlayer();
				//roll for the player and set their turn
				Random rand = new Random();
				int randRoll = rand.nextInt(6) + 1;
				setTurn(currentPlayer, randRoll);
				//if player is human, start their turn by calculating targets
				if (currentPlayer instanceof HumanPlayer) {
					HumanPlayer currentHumanPlayer = (HumanPlayer) currentPlayer;
					currentHumanPlayer.startTurn();
				} else {
					//if player is computer just move them to a random possible position
					moveComputerPlayer();
				}
			}
		}	
	}
	
	public void moveComputerPlayer() {
		//get the current player
		ComputerPlayer currentComputerPlayer = (ComputerPlayer) Board.getInstance().getCurrentPlayer();
		BoardCell destinationCell = currentComputerPlayer.selectTarget();
			
			//testing block below, uncomment to verify that the computer wins when it has all the solution cards
			/*
			currentComputerPlayer.getLastSuggestion().clear();
			currentComputerPlayer.getLastSuggestion().add(Board.getInstance().getSolution().getWeapon());
			currentComputerPlayer.getLastSuggestion().add(Board.getInstance().getSolution().getPerson());
			currentComputerPlayer.getLastSuggestion().add(Board.getInstance().getSolution().getRoom());
			*/
			
			//checks if computer will accuse, if it does, check solution and if win, end game
		if(!currentComputerPlayer.makeAccusation().isEmpty()) {
			if(currentComputerPlayer.getLastSuggestion().get(0).equals(Board.getInstance().getSolution().getWeapon()) && 
			   currentComputerPlayer.getLastSuggestion().get(1).equals(Board.getInstance().getSolution().getPerson()) && 
				  currentComputerPlayer.getLastSuggestion().get(2).equals(Board.getInstance().getSolution().getRoom())) {
				
					JOptionPane.showMessageDialog(null, currentComputerPlayer.getName() + " has won the game" + '\n'
					+ "The accusation was: " + Board.getInstance().getSolution().getPerson().getCardName() + " in " + Board.getInstance().getSolution().getRoom().getCardName() + " with the " + Board.getInstance().getSolution().getWeapon().getCardName());
					System.exit(0);
			}
				
		}
			
		//set the old cell's occupation status to false
		Board.getInstance().getGrid()[currentComputerPlayer.getRow()][currentComputerPlayer.getCol()].setOccupied(false);
		//set the new cell they moved to as occupied
		destinationCell.setOccupied(true);
		currentComputerPlayer.moveComputer(destinationCell);
		//if the cell is a room, have the computer make a suggestion
		if (destinationCell.isRoom()) {
			ArrayList<Card> suggestion = currentComputerPlayer.computerSuggestion();
			Card suggestionResult = Board.getInstance().handleSuggestion(currentComputerPlayer, destinationCell.getRoom(), suggestion.get(0), suggestion.get(1));
			//update the game control panel
			setGuess(suggestion.get(1).getCardName() + ", " + destinationCell.getRoom().getName() + ", " + suggestion.get(0).getCardName());
			//move the player that was in the suggestion to the current room
			Player suggestedPlayer = suggestion.get(1).getPlayerFromCard();
			Board.getInstance().getGrid()[suggestedPlayer.getRow()][suggestedPlayer.getCol()].setOccupied(false);
			suggestedPlayer.setRow(destinationCell.getRow());
			suggestedPlayer.setCol(destinationCell.getCol());
			suggestedPlayer.movingFromSuggestion();
			Board.getInstance().getGrid()[suggestedPlayer.getRow()][suggestedPlayer.getCol()].setOccupied(true);
			//below is functionality that adds the room a player is dragged to into its roomsVisited list
			//this should stay commented as the code logically makes no sense
			//uncommenting this adds a chance of softlocking the CPU
			/*
			if(suggestedPlayer instanceof ComputerPlayer) {
				((ComputerPlayer) suggestedPlayer).getRoomsVisited().add(destinationCell);
			} 
			*/
			Board.getInstance().repaint();
			if (suggestionResult == null) {
				setGuessResult("No new clue");
			} else {
				setGuessResult("Suggestion disproven!");
			}
		}
	}
	public class AccusationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			HumanPlayer humanPlayer = (HumanPlayer) Board.getInstance().getHumanPlayer();
			//see if the human player's turn is over
			if (humanPlayer.isTurnOver()) {
				JOptionPane.showMessageDialog(null, "It is not your turn!");
			} else {
				//if the turn is not over, allow for an accusation
				AccusationDialog playerAccusation = new AccusationDialog();
				playerAccusation.setModal(true);
				playerAccusation.setVisible(true);
			}
			
		}
	}
	public class AccusationDialog extends JDialog {
		private JComboBox<String> playersCombo = Board.getInstance().createPlayersCombo();
		private JComboBox<String> weaponsCombo = Board.getInstance().createWeaponsCombo();
		private JComboBox<String> roomsCombo = Board.getInstance().createRoomsCombo();
		public AccusationDialog() {
			//create the dialog
			setTitle("Make an accusation");
			setSize(300,300);
			setLayout(new GridLayout(0,2));

			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new GridLayout(4,0));
			JLabel roomLabel = new JLabel("Room");
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
			rightPanel.add(roomsCombo);
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
				String accusedPlayerName = (String) playersCombo.getSelectedItem();
				String accusedWeaponName = (String) weaponsCombo.getSelectedItem();
				String accusedRoomName = (String) roomsCombo.getSelectedItem();
				Card accusedPlayer = Board.getInstance().getPlayerCardFromString(accusedPlayerName);
				Card accusedWeapon = Board.getInstance().getWeaponCardFromString(accusedWeaponName);
				Card accusedRoom = Board.getInstance().getRoomCardFromString(accusedRoomName);
				//process the accusation
				boolean accusationResult = Board.getInstance().handleAccusation(accusedRoom, accusedWeapon, accusedPlayer);
				if (!accusationResult) {
					JOptionPane.showMessageDialog(null,  accusedPlayer.getCardName() + " in " + accusedRoom.getCardName()
					+ " with the " + accusedWeapon.getCardName() + " was incorrect. You lose!");
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(null,  accusedPlayer.getCardName() + " in " + accusedRoom.getCardName()
					+ " with the " + accusedWeapon.getCardName() + " was correct. Congrats, you win!");
					System.exit(0);
				}
				dispose();
			}
			
		}
	}

	
	/**
	 * Main to test the panel
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameControlPanel panel = new GameControlPanel(null);  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(750, 180);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible
		
		// test filling in the data
		panel.setTurn(new ComputerPlayer( "Lady Maria of the Astral Clocktower", Color.RED, 0, 0), 4);
		panel.setGuess( "I have no guess!");
		panel.setGuessResult( "So you have nothing?");
	}
}
