package clueGame;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Random;

import clueGame.GameControlPanel.NextListener;

import java.awt.Graphics;

public class HumanPlayer extends Player {
	private boolean turnOver = false;

	public HumanPlayer(String name, Color color, int row, int column) {
		super(name, color, row, column);
	}

	public void selectTarget(int col, int row) {
		super.setCol(col);
		super.setRow(row);
	}
	public void startTurn() {
		
		getHumanTargets();
		Board.getInstance().repaint();
		turnOver = false;
	}

	public void turnFinished() {
		turnOver = true;
	}
	
	public boolean isTurnOver() {
		return turnOver;
	}

	@Override
	public BoardCell selectTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	private void getHumanTargets() {
		Board.getInstance().startCalcTargets(Board.getInstance().getCell(super.getRow(), super.getCol()), super.getRoll());
	}


}
