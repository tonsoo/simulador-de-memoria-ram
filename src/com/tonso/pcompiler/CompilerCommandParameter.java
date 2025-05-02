package com.tonso.pcompiler;

public class CompilerCommandParameter {
	private int type = -1;
	private int parameterIndex = 0;
	private String defaultValue = "";
	
	public CompilerCommandParameter(int type, int parameterIndex) {
		this.type = type;
		this.parameterIndex = parameterIndex;
		this.defaultValue = "";
	}
	
	public CompilerCommandParameter(String defaultValue, int parameterIndex) {
		this.type = -1;
		this.defaultValue = defaultValue;
		this.parameterIndex = parameterIndex;
	}
	
	public String DefaultValue() {
		return this.defaultValue;
	}
	
	public int Type() {
		return this.type;
	}
	
	public int ParameterIndex() {
		return this.parameterIndex;
	}
}
