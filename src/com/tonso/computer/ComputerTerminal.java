package com.tonso.computer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.tonso.graphics.Screen;

public class ComputerTerminal extends Screen implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int[] forbiddenCharacterCodes = new int[] {};
	
	private boolean blinked = false, hasFocus;
	
	private int blinkDelay = 100, blinkCounter=0;
	
	private Font commandLineFont;
	
	private String commandLine = "";
	
	private static List<TerminalLine> previousCommands;
	
	private Processor processor;
	
	private int previousCommandIndex = -1;

	public ComputerTerminal(int width, int height, Processor processor) {
		super(width, height, "Terminal", JFrame.EXIT_ON_CLOSE);
		
		this.addKeyListener(this);
		
		commandLineFont = new Font("Arial", Font.PLAIN, 14);
		
		previousCommands = new ArrayList<TerminalLine>();
		
		this.processor = processor;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void FixedUpdate() {
		blinkDelay = 30;
		if(hasFocus != this.hasFocus()) {
			blinked = false;
		}
		hasFocus = this.hasFocus();
		if(this.hasFocus()) {
			blinkCounter++;
			
			if(blinkCounter >= blinkDelay) {
				blinkCounter = 0;
				blinked = !blinked;
			}
		}
	}

	@Override
	public void Render(Graphics g) {
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, width, height);
		
		g.setColor(new Color(23, 23, 23));
		
		g.setFont(commandLineFont);
				
		g.fillRect(5, height - 25, width - 20, 20);
		
		g.setColor(new Color(200, 200, 200));
		
		g.drawString(commandLine, 8, height - 25 + commandLineFont.getSize());
		
		int commandWidth = g.getFontMetrics().stringWidth(commandLine);
		if(this.hasFocus() == true && blinked) {
			g.fillRect(8 + commandWidth, height - 23, 3, 16);
		}
		
		for(int i = 0; i < previousCommands.size(); i++) {
			switch(previousCommands.get(i).type) {
			case 0:
				g.setColor(new Color(200, 200, 200));
				break;
			case 1:
				g.setColor(new Color(200, 30, 20));
				break;
			}
			String prevCommand = previousCommands.get(i).command;
			g.drawString(prevCommand, 8, height - 25 - commandLineFont.getSize() * (previousCommands.size() - i - 1) - 10 - 2 * (previousCommands.size() - i - 1));
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 8) {
			if(commandLine.length() > 0) {				
				commandLine = commandLine.substring(0, commandLine.length() - 1);
			}
			return;
		} else if(e.getKeyCode() == 10) {
			if(commandLine.equals("/cls")) {
				previousCommands.clear();
				commandLine = "";
				return;
			}
			previousCommandIndex = -1;
			List<String> errors = new ArrayList<String>();
			previousCommands.add(new TerminalLine(commandLine, 0));
			
			if(processor != null) {
				if(!processor.ExecuteCommand(commandLine)) {
					errors.add(processor.LastErrorMessage());
				}
			} else {
				errors.add("Processor was not instanciated.");
			}
			
			for(String err : errors) {
				ErrorOut(err);
			}
			commandLine = "";
		} else if(e.getKeyCode() == 38 && previousCommandIndex < previousCommands.size() - 1) {
			int startIndex = previousCommandIndex;
			do {
				previousCommandIndex++;
				if(previousCommandIndex > previousCommands.size() - 1) {
					previousCommandIndex = startIndex;
					break;
				}
			} while(previousCommands.get(previousCommands.size() - previousCommandIndex - 1).type != 0);
			
			commandLine = previousCommands.get(previousCommands.size() - previousCommandIndex - 1).command;
		} else if(e.getKeyCode() == 40 && previousCommandIndex >= 0) {
			int startIndex = previousCommandIndex;
			do {				
				previousCommandIndex--;
				if(previousCommandIndex < 0) {
					previousCommandIndex = startIndex;
					break;
				}
			} while(previousCommands.get(previousCommands.size() - previousCommandIndex - 1).type != 0);
			
			if(previousCommandIndex >= 0) {				
				commandLine = previousCommands.get(previousCommands.size() - previousCommandIndex - 1).command;
			} else {
				commandLine = "";
			}
		}
		
		for(int i = 0; i < forbiddenCharacterCodes.length; i++) {
			if(e.getKeyCode() == forbiddenCharacterCodes[i]) {
				return;
			}
		}
		
		if(e.getKeyCode() < 41 && e.getKeyCode() != 32) {
			return;
		}
		
		commandLine += e.getKeyChar();
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	public static void ErrorOut(String message) {
		previousCommands.add(new TerminalLine(message, 1));
	}
}
