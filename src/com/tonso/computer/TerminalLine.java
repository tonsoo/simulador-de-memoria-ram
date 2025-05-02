package com.tonso.computer;

public class TerminalLine {
	public String command;
	int type = 0; // 0 out; 1 err
	
	public TerminalLine(String command, int type) {
		this.command = command;
		this.type = type;
	}
}
