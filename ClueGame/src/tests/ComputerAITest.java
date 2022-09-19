//Authors: Alex Yen, Grant Renwick
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class ComputerAITest {
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
	public void checkCompTargets() {
		//test if computer enters room it hasnt seen before
		board.getPlayers().get(1).setRow(7);	//initialize where the computer player starts to make sure that they always enter a room
		board.getPlayers().get(1).setCol(22);
		assertEquals(board.getRoom('B').getCenterCell(), board.getPlayers().get(1).selectTarget());
		//test if random board space is chosen if no room in list
		board.getPlayers().get(2).setRow(16);	//initialize where the computer player starts to make sure that they never enter a room
		board.getPlayers().get(2).setCol(0);
		assertTrue(!board.getPlayers().get(2).selectTarget().isRoom());
		//test if the computer chooses not to enter a room if it has been visited
		board.getPlayers().get(3).setRow(20);	//initialize where the computer player starts to make sure that they never enter a room
		board.getPlayers().get(3).setCol(22);
		//below edits visited to contain the room that the player is next to
		Set<BoardCell> visitedTest = new HashSet<BoardCell>();
		visitedTest.add(board.getCell(21, 23));
		board.setVisited(visitedTest);
		assertTrue(!board.getPlayers().get(3).selectTarget().isRoom());
	}
	
	@Test
	public void testComputerSuggestion() {
		//Clear players and set up 3 players for tests
		HumanPlayer testHuman = new HumanPlayer("Solaire of Astora", Color.yellow, 0, 17);
		ComputerPlayer computerOne = new ComputerPlayer("Morgott the Omen King", Color.YELLOW.darker().darker().darker(), 3, 21);
		ComputerPlayer computerTwo = new ComputerPlayer("Unbreakable Patches", Color.BLACK, 22, 16);
		board.clearPlayers();
		board.clearPlayerCards();
		board.clearWeaponCards();
		board.addPlayer(testHuman);
		board.addPlayer(computerOne);
		board.addPlayer(computerTwo);
		Card testRoom = new Card("Gunfort", CardType.ROOM);
		Card testPerson = new Card("Isshin Ashina", CardType.PERSON);
		Card testWeapon = new Card("Mortal Blade", CardType.WEAPON);
		testHuman.setHand(testRoom);
		testHuman.setHand(testPerson);
		testHuman.setHand(testWeapon);
		Board.getInstance().addPlayerCard(testPerson);
		Board.getInstance().addWeaponCard(testWeapon);
		Card testRoom2 = new Card("Senpou Temple", CardType.ROOM);
		Card testPerson2 = new Card("Lady Maria of the Astral Clocktower", CardType.PERSON);
		Card testWeapon2 = new Card("Moonlight Greatsword", CardType.WEAPON);
		computerOne.setHand(testWeapon2);
		computerOne.setHand(testPerson2);
		computerOne.setHand(testRoom2);
		Board.getInstance().addPlayerCard(testPerson2);
		Board.getInstance().addWeaponCard(testWeapon2);
		Card testRoom3 = new Card("Ash Lake", CardType.ROOM);
		Card testPerson3 = new Card("Siegward of Catarina", CardType.PERSON);
		Card testWeapon3 = new Card("Zweihander", CardType.WEAPON);
		computerTwo.setHand(testWeapon3);
		computerTwo.setHand(testPerson3);
		computerTwo.setHand(testRoom3);
		Board.getInstance().addPlayerCard(testPerson3);
		Board.getInstance().addWeaponCard(testWeapon3);
		
		//test that checks that the weapon card picked was one of the two weapons not in the revealed cards
		Card randomWeaponCheck = computerOne.computerSuggestion().get(0);
		assertTrue(randomWeaponCheck.equals(new Card("Mortal Blade", CardType.WEAPON)) || 
				randomWeaponCheck.equals(new Card("Zweihander", CardType.WEAPON)));
		//test that checks that the player card picked was one of the two players not in the revealed cards
		Card randomPlayerCheck = computerOne.computerSuggestion().get(1);
		assertTrue(randomPlayerCheck.equals(new Card("Siegward of Catarina", CardType.PERSON)) || 
				randomPlayerCheck.equals(new Card("Isshin Ashina", CardType.PERSON)));
		
		//test that checks that the correct room card was generated based on room that computer is in
		assertTrue(computerOne.computerSuggestion().get(2).equals(new Card("Blighttown", CardType.ROOM)));
		//add a weapon to revealed cards
		computerOne.addRevealedCard(testWeapon3);
		//test that checks that the weapon selected is the only weapon in the possible pool of weapons
		assertTrue(computerOne.computerSuggestion().get(0).equals(new Card("Mortal Blade", CardType.WEAPON)));
		//add a player to revealed cards
		computerOne.addRevealedCard(testPerson);
		//test that checks that the player selected is the only player in the possible pool of players
		assertTrue(computerOne.computerSuggestion().get(1).equals(new Card("Siegward of Catarina", CardType.PERSON)));
		
		

	}
}
