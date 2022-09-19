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

public class BoardAdjTargetTest {
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
	public void testTargetsLondo() {//test leaving normally
		//test a roll of 1
		board.startCalcTargets(board.getCell(3, 12), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(3, 8)));
		assertTrue(targets.contains(board.getCell(3, 16)));
		assertTrue(targets.contains(board.getCell(7, 12)));
		//test a roll of 2
		board.startCalcTargets(board.getCell(3, 12), 2);
		targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(3, 7)));
		assertTrue(targets.contains(board.getCell(3, 17)));
		assertTrue(targets.contains(board.getCell(7, 11)));
		assertTrue(targets.contains(board.getCell(7, 13)));
		//test a roll of 3
		board.startCalcTargets(board.getCell(3, 12), 3);
		targets = board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCell(2, 7)));
		assertTrue(targets.contains(board.getCell(3, 6)));
		assertTrue(targets.contains(board.getCell(4, 7)));
		assertTrue(targets.contains(board.getCell(7, 10)));
		assertTrue(targets.contains(board.getCell(7, 14)));
		assertTrue(targets.contains(board.getCell(2, 17)));
		assertTrue(targets.contains(board.getCell(3, 18)));
		assertTrue(targets.contains(board.getCell(4, 17)));
	}
	
	@Test
	public void testTargetsLake() {//test secret passage
		//test a roll of 1, leaving through doors and secret passage
		board.startCalcTargets(board.getCell(3, 2), 1);
		Set<BoardCell> targets= board.getTargets();
		//assertEquals(3, targets.size());
		
		assertTrue(targets.contains(board.getCell(0, 6)));
		assertTrue(targets.contains(board.getCell(2, 6)));
		assertTrue(targets.contains(board.getCell(20, 12)));
	}
	
	@Test
	public void testTargetsWalkway() {//tests that walkways work correctly
		//test a roll of 1
		board.startCalcTargets(board.getCell(7, 7), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(7, 6)));
		assertTrue(targets.contains(board.getCell(7, 8)));
		assertTrue(targets.contains(board.getCell(6, 7)));
		assertTrue(targets.contains(board.getCell(8, 7)));
		//test a roll of 3
		board.startCalcTargets(board.getCell(7, 7), 3);
		targets= board.getTargets();
		assertEquals(12, targets.size());
		assertTrue(targets.contains(board.getCell(7, 6)));
		assertTrue(targets.contains(board.getCell(7, 8)));
		assertTrue(targets.contains(board.getCell(6, 7)));
	}
	
	@Test 
	public void testTargetsDoor() {
		//test a roll of 1
		board.startCalcTargets(board.getCell(11, 18), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(11, 21)));
		assertTrue(targets.contains(board.getCell(11, 17)));
		assertTrue(targets.contains(board.getCell(10, 18)));
		assertTrue(targets.contains(board.getCell(12, 18)));
	}
	
	@Test
	public void testTargetsOccupied() {//tests that players can't travel through each other
		//test a roll of 1
		board.getCell(8, 1).setOccupied(true);
		board.startCalcTargets(board.getCell(8, 0), 1);
		board.getCell(8, 1).setOccupied(false);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCell(7, 0)));	
		
		//test that plater can't leave through an occupied door
		board.getCell(2, 6).setOccupied(true);
		board.startCalcTargets(board.getCell(3, 2), 1);
		board.getCell(2, 6).setOccupied(false);
		targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(0, 6)));	
		assertTrue(targets.contains(board.getCell(20, 12)));	
		
		// we want to make sure we can get into a room, even if flagged as occupied
		board.getCell(3, 2).setOccupied(true);
		board.getCell(1, 6).setOccupied(true);
		board.startCalcTargets(board.getCell(2, 6), 1);
		board.getCell(3, 2).setOccupied(false);
		board.getCell(1, 6).setOccupied(false);
		targets= board.getTargets();
		//assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(2, 7)));	
		assertTrue(targets.contains(board.getCell(3, 6)));	
		assertTrue(targets.contains(board.getCell(3, 2)));	
	}
	//Test that checks walkways are correctly adjacent to other cells
	@Test
	public void testAdjacentWalkway() {
		//First test a cell that should only have walkways as adjacent cells
		Set<BoardCell> testList = board.getAdjList(7, 6);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(7, 7)));
		assertTrue(testList.contains(board.getCell(7, 5)));
		assertTrue(testList.contains(board.getCell(8, 6)));
		assertTrue(testList.contains(board.getCell(6, 6)));
		
		//Next test a walkway that is next to a room
		testList = board.getAdjList(7, 0);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(7, 1)));
		assertTrue(testList.contains(board.getCell(8, 0)));
		
	}
	@Test
	public void testAdjacenciesRoom() {
		//Test a room that has a secret passage
		Set<BoardCell> testList = board.getAdjList(20, 12);
		assertEquals(7, testList.size());
		assertTrue(testList.contains(board.getCell(16, 11)));
		assertTrue(testList.contains(board.getCell(16, 12)));
		assertTrue(testList.contains(board.getCell(16, 13)));
		assertTrue(testList.contains(board.getCell(21, 8)));
		assertTrue(testList.contains(board.getCell(21, 16)));
		assertTrue(testList.contains(board.getCell(3, 21)));
		assertTrue(testList.contains(board.getCell(3, 2)));
		
		//Test a room that only has one door
		testList = board.getAdjList(11, 21);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(11, 18)));
	}
	@Test
	public void testNotRoomCenter() {
		//test a room cell that isn't the center
		Set<BoardCell> testList = board.getAdjList(4, 23);
		assertEquals(0, testList.size());
		//test another
		testList = board.getAdjList(3, 1);
		assertEquals(0, testList.size());
	}
	@Test
	public void testCellEdges() {
		//Test a walkway on the left edge
		Set<BoardCell> testList = board.getAdjList(8, 0);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(7, 0)));
		assertTrue(testList.contains(board.getCell(8, 1)));
		//Test a walkway on the top edge
		testList = board.getAdjList(0, 17);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(1, 17)));
		assertTrue(testList.contains(board.getCell(0, 18)));
		//Test a walkway on the bottom edge
		testList = board.getAdjList(22, 8);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(21, 8)));
		assertTrue(testList.contains(board.getCell(22, 7)));
		//Test a walkway on the right edge
		testList = board.getAdjList(8, 24);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(7, 24)));
		assertTrue(testList.contains(board.getCell(9, 24)));
		assertTrue(testList.contains(board.getCell(8, 23)));
	}
	
	@Test
	public void testDoors() {
		//Test a door's adjacency list to make sure it is connected to a room's center
		Set<BoardCell> testList = board.getAdjList(0, 6);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(3, 2)));
		assertTrue(testList.contains(board.getCell(0, 7)));
		assertTrue(testList.contains(board.getCell(1, 6)));
		
		//Test another
		testList = board.getAdjList(11,18);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(10, 18)));
		assertTrue(testList.contains(board.getCell(11, 17)));
		assertTrue(testList.contains(board.getCell(12, 18)));
		assertTrue(testList.contains(board.getCell(11, 21)));

	}
}
