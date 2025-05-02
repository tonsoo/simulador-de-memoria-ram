package com.tonso.pcompiler;

public class CompilerLine {
	private String name;
	private int lineIndex;
	
	public CompilerLine(String name, int lineIndex) {
		this.name = name;
		this.lineIndex = lineIndex;
	}
	
	public String getName() { return name; }
	public int getLine() { return lineIndex; }
}
