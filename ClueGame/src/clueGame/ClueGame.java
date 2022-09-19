//Authors: Alex Yen, Grant Renwick
package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClueGame extends JFrame {
	public ClueGame() {
		
	}
	
	public static void main(String args[]) {
		Board board;
		
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load config files 
		board.initialize();
		//create a overall jframe
		JFrame boardFrame = new JFrame();
		//create the side panel and bottom panel
		KnownCardsPanel sidePanel = new KnownCardsPanel();
		board.setRevealedCardsPanel(sidePanel);
		GameControlPanel bottomPanel = new GameControlPanel(sidePanel);
		board.setGameControlPanel(bottomPanel);
		Random rand = new Random();
		int randRoll = rand.nextInt(6) + 1;
		//set up the first turn
		bottomPanel.setTurn(board.getCurrentPlayer(), randRoll);
		HumanPlayer firstPlayer = (HumanPlayer) Board.getInstance().getPlayers().get(0);
		firstPlayer.startTurn();

		//add the panels to the frame
		boardFrame.add(board, BorderLayout.CENTER);
		boardFrame.add(bottomPanel, BorderLayout.SOUTH);
		boardFrame.add(sidePanel, BorderLayout.EAST);
		
		//give the frame a title
		boardFrame.setTitle("CSCI306 Clue Game");	
		boardFrame.setSize(950, 750);  // size the frame
		

		boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		boardFrame.setVisible(true); // make it visible
		//display the intro message
		JOptionPane.showMessageDialog(null, "You are " + board.getHumanPlayer().getName() + ". \nCan you find the solution \nbefore the Computer players?");
	}
}


