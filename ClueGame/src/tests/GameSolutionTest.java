//Authors: Alex Yen, Grant Renwick
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import clueGame.Player;
import clueGame.Room;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class GameSolutionTest {
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
	public void checkSolution() {

		Card tempRoom = new Card("Anor Londo", CardType.ROOM);
		Card tempPerson = new Card("Unbreakable Patches", CardType.PERSON);
		Card tempWeapon = new Card("Zweihander", CardType.WEAPON);
		board.getSolution().newSolution(tempRoom, tempPerson, tempWeapon);
		assertTrue(board.getSolution().getPerson().equals(tempPerson));
		assertTrue(board.getSolution().getWeapon().equals(tempWeapon));
		assertTrue(board.getSolution().getRoom().equals(tempRoom));

	}
	
	@Test
	public void testAccusation() {
		//create a known solution
		Card testRoom = new Card("Gunfort", CardType.ROOM);
		Card testPerson = new Card("Isshin Ashina", CardType.PERSON);
		Card testWeapon = new Card("Mortal Blade", CardType.WEAPON);
		board.getSolution().newSolution(testRoom, testPerson, testWeapon);
		//Test that accusation is correct
		assertTrue(board.checkAccusation(testWeapon, testRoom, testPerson));
		//Test an accusation with an incorrect room
		testRoom = new Card("Anor Londo", CardType.ROOM);
		assertFalse(board.checkAccusation(testWeapon, testRoom, testPerson));
		testRoom = new Card("Gunfort", CardType.ROOM);
		//Test an accusation with an incorrect player
		testPerson = new Card("Siegward of Catarina", CardType.PERSON);
		assertFalse(board.checkAccusation(testWeapon, testRoom, testPerson));
		testPerson = new Card("Isshin Ashina", CardType.PERSON);
		//Test an accusation with an incorrect weapon
		testWeapon = new Card("Zweihander", CardType.WEAPON);
		assertFalse(board.checkAccusation(testWeapon, testRoom, testPerson));
		testRoom = new Card("Anor Londo", CardType.ROOM);
		//Test an accusation with an no correct cards
		testPerson = new Card("Siegward of Catarina", CardType.PERSON);
		assertFalse(board.checkAccusation(testWeapon, testRoom, testPerson));
	}
	
	@Test
	public void testDisputeSuggestion() {
		//create cards to put in hand of a test player
		Card testRoom = new Card("Gunfort", CardType.ROOM);
		Card testPerson = new Card("Isshin Ashina", CardType.PERSON);
		Card testWeapon = new Card("Mortal Blade", CardType.WEAPON);
		
		//create a test player and set their hand
		ComputerPlayer disputePerson = new ComputerPlayer("Morgott the Omen King", Color.YELLOW.darker().darker().darker(), 22, 17);
		disputePerson.setHand(testWeapon);
		disputePerson.setHand(testPerson);
		disputePerson.setHand(testRoom);
		
		//create cards that are not in a person's hand
		Card wrongRoom = new Card("Senpou Temple", CardType.ROOM);
		Card wrongPerson = new Card("Unbreakable Patches", CardType.PERSON);
		Card wrongWeapon = new Card("Solaire of Astora", CardType.WEAPON);
		

		
		//test that tests a dispute with all 3 cards being correct
		assertTrue(disputePerson.disputeSuggestion(testWeapon, testRoom, testPerson) != null);
		//test that tests a dispute with only 2 cards correct
		Card disputeTest = disputePerson.disputeSuggestion(testWeapon, testRoom, wrongPerson);
		assertTrue(disputeTest.equals(testWeapon) || disputeTest.equals(testRoom));
		//test that tests a dispute with only one card correct
		assertTrue(disputePerson.disputeSuggestion(testWeapon, wrongRoom, wrongPerson).equals(testWeapon));
		//test that tests a dispute with no matching cards
		assertTrue(disputePerson.disputeSuggestion(wrongWeapon, wrongRoom, wrongPerson) == null);
	}
	
	@Test
	public void testHandledSuggestion() {
		//Clear players and set up 3 players for tests
		HumanPlayer testHuman = new HumanPlayer("Solaire of Astora", Color.yellow, 0, 17);
		ComputerPlayer computerOne = new ComputerPlayer("Morgott the Omen King", Color.YELLOW.darker().darker().darker(), 22, 17);
		ComputerPlayer computerTwo = new ComputerPlayer("Unbreakable Patches", Color.BLACK, 22, 16);
		board.clearPlayers();
		board.addPlayer(testHuman);
		board.addPlayer(computerOne);
		board.addPlayer(computerTwo);
		Card testRoom = new Card("Gunfort", CardType.ROOM);
		Card testPerson = new Card("Isshin Ashina", CardType.PERSON);
		Card testWeapon = new Card("Mortal Blade", CardType.WEAPON);
		testHuman.setHand(testRoom);
		testHuman.setHand(testPerson);
		testHuman.setHand(testWeapon);
		Card testRoom2 = new Card("Senpou Temple", CardType.ROOM);
		Card testPerson2 = new Card("Lady Maria of the Astral Clocktower", CardType.PERSON);
		Card testWeapon2 = new Card("Moonlight Greatsword", CardType.WEAPON);
		computerOne.setHand(testWeapon2);
		computerOne.setHand(testPerson2);
		computerOne.setHand(testRoom2);
		Card testRoom3 = new Card("Ash Lake", CardType.ROOM);
		Card testPerson3 = new Card("Siegward of Catarina", CardType.PERSON);
		Card testWeapon3 = new Card("Zweihander", CardType.WEAPON);
		computerTwo.setHand(testWeapon3);
		computerTwo.setHand(testPerson3);
		computerTwo.setHand(testRoom3);
		
		//test a card where the card wanted is in the hand of the suggestor
		assertTrue(board.handleSuggestion(testHuman, new Room("Gunfort"), new Card("Rivers of Blood", CardType.WEAPON)
				, new Card("Unbreakable Patches", CardType.PERSON)) == null);
		//test a card where all players have a card wanted, but only the first player's card after the suggestor is returned
		assertTrue(board.handleSuggestion(computerOne, new Room("Ash Lake"), new Card("Rivers of Blood", CardType.WEAPON)
				, new Card("Isshin Ashina", CardType.PERSON)).equals(new Card("Ash Lake", CardType.ROOM)));
		//test a card where the card wanted is in the hand of the suggestor
		assertTrue(board.handleSuggestion(computerTwo, new Room("Ash Lake"), new Card("Rivers of Blood", CardType.WEAPON)
				, new Card("Unbreakable Patches", CardType.PERSON)) == null);
		//test a card where no one has the card, so null is returned
		assertTrue(board.handleSuggestion(computerTwo, new Room("Firelink Shrine"), new Card("Rivers of Blood", CardType.WEAPON)
				, new Card("Unbreakable Patches", CardType.PERSON)) == null);
		//test that only human can disprove
		assertTrue(board.handleSuggestion(computerTwo, new Room("Gunfort"), new Card("Rivers of Blood", CardType.WEAPON)
				, new Card("Unbreakable Patches", CardType.PERSON)).equals(new Card("Gunfort", CardType.ROOM)));
	}
	

}
