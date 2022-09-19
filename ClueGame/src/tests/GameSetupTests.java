//Authors: Alex Yen, Grant Renwick
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.CardType;


public class GameSetupTests {
	// We make the Board static because we can load it one time and 
	// then do all the tests. 
	private static Board board;
	
	@BeforeEach
	public void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load config files 
		board.initialize();
	}
	
	@Test
	public void testPlayerLoads() {
		//test that makes sure the correct number of players were loaded and that two specific players were loaded
		assertEquals(6, board.getPlayers().size());
		assertTrue(board.getPlayers().get(0).getName().equals("Solaire of Astora"));
		assertTrue(board.getPlayers().get(5).getName().equals("Siegward of Catarina"));
	}
	
	@Test
	public void testDeckAll() {	//tests for full deck of cards being generated
		assertEquals(21, board.getCardCount());
	}
	
	@Test
	public void testSolution() {	//tests that the solution has a valid set of cards
		assertEquals(CardType.ROOM , board.getSolution().getRoom().getCardType());
		assertEquals(CardType.WEAPON , board.getSolution().getWeapon().getCardType());
		assertEquals(CardType.PERSON , board.getSolution().getPerson().getCardType());
	}
	
	@Test
	public void testWeapons() {
		//checks to make sure 6 weapons were correctly loaded and that two specific weapons were loaded
		assertEquals(6, board.getWeapons().size());
		assertTrue(board.getWeapons().get(0).equals("Mortal Blade"));
		assertTrue(board.getWeapons().get(5).equals("Washing Pole"));
	}
	
	@Test
	public void checkHandExists() {		//tests that the dealing of the cards is valid
//test that ensures that each player has 3 cards in their hand
		assertEquals(3, board.getPlayers().get(0).getHand().size());
		assertEquals(3, board.getPlayers().get(1).getHand().size());
		assertEquals(3, board.getPlayers().get(2).getHand().size());
		assertEquals(3, board.getPlayers().get(3).getHand().size());
		assertEquals(3, board.getPlayers().get(4).getHand().size());
		assertEquals(3, board.getPlayers().get(5).getHand().size());
		assertEquals(0, board.getCards().size());	//makes sure deck is empty once all cards have been dealt
	}
	
}
