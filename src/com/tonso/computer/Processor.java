package com.tonso.computer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import javax.swing.JFrame;

import com.tonso.graphics.Screen;
import com.tonso.pcompiler.CompilerLine;
import com.tonso.pcompiler.CompilerSyntaxException;
import com.tonso.pcompiler.ProgramCompiler;

public class Processor extends Screen {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6488197419141117642L;
	
	private MemoryByte[] registers = new MemoryByte[4];
	private boolean[] comparisonBit = new boolean[] { false, false }; 
	
	private Memory memory;
	
	private Font registerFont;
	
	private String commandError = "";
	
	private boolean ExecutingCommandFromFile = false;
	private int currentCommandFromFile = 0;
	private List<CompilerLine> commandsFromFile;
	
	private static final int normal_width = 300, normal_height = 300;
	private static final int command_panel_width = 600, command_panel_height = 300;

	public Processor(Memory memory) {
		super(normal_width, normal_height, "Processor Panel", JFrame.EXIT_ON_CLOSE);
		
		this.memory = memory;
		
		registers = new MemoryByte[4];
		
		for(int i = 0; i < registers.length; i++) {
			registers[i] = new MemoryByte();
		}
		
		registerFont = new Font("Arial", Font.PLAIN, 16);
	}

	int scrollYOffset = 0, scrollXOffset = 0;
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(commandsFromFile == null) {
			return;
		}
		if(commandsFromFile.size() <= 0) {
			return;
		}
		int scrollAux = 0, scrollIncreaseTimeout = 100, scrollMultiplier = 2;
		if(e.getX() < command_panel_width && e.getX() > normal_width) {			
			if(scrollAux <= scrollIncreaseTimeout) {
				scrollMultiplier++;
			} else {
				scrollMultiplier = 1;
				scrollAux = 0;
			}
			if(scrollMultiplier > 25) {
				scrollMultiplier = 25;
			}
			
			if(e.isAltDown()) {
				scrollMultiplier = 75;
			}
			
			if(e.isShiftDown() && e.isAltDown()) {
				scrollMultiplier = 200;
			}
			
			if(!e.isControlDown()) {				
				scrollYOffset -= e.getWheelRotation() * scrollMultiplier;
			} else {
				scrollXOffset += e.getWheelRotation() * scrollMultiplier;
			}
		}
		System.out.println("x: "+scrollXOffset+" | y: "+scrollYOffset);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(commandsFromFile == null) {
			return;
		}
		if(commandsFromFile.size() <= 0) {
			CloseFile();
			return;
		}
		if(e.getX() > normal_width && e.getX() < command_panel_width) {
			if(currentCommandFromFile >= commandsFromFile.size()) {
				CloseFile();
			} else {
				ExecutingCommandFromFile = true;
				if(ExecuteCommand(commandsFromFile.get(currentCommandFromFile).getName()) == false) {
					ComputerTerminal.ErrorOut(this.LastErrorMessage());
					CloseFile();
				}
				ExecutingCommandFromFile = false;
			}
			currentCommandFromFile++;
		}
	}

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
	public void FixedUpdate() {}

	@Override
	public void Render(Graphics g) {
		int registerHeight = registerFont.getSize();
		g.setFont(registerFont);
		
		int nextLine = 0;
		
		if(commandsFromFile != null) {
			g.setColor(new Color(0, 0, 0));
			g.fillRect(normal_width, 0, command_panel_width - normal_width, command_panel_height);
			
			if(commandsFromFile.size() > 0) {
				int paddingLeft = g.getFontMetrics().stringWidth((commandsFromFile.get(commandsFromFile.size()-1).getLine()) + "");
			
				int maxTextWidth = 0;
				for(int i = 0; i < commandsFromFile.size(); i++) {
					int textWidth = g.getFontMetrics().stringWidth(commandsFromFile.get(i).getName());
					if(textWidth > maxTextWidth) {
						maxTextWidth = textWidth;
					}
				}
				if(scrollXOffset > 0 || maxTextWidth < command_panel_width) {
					scrollXOffset = 0;
				}
				if(scrollYOffset < 0) {
					scrollYOffset = 0;
				} else if(scrollYOffset > -(registerFont.getSize() + 5) * (commandsFromFile.size() + 1) + command_panel_height) {
					scrollYOffset = -(registerFont.getSize() + 5) * (commandsFromFile.size() + 1) + command_panel_height;
				}
				
				if((registerFont.getSize() + 5) * (commandsFromFile.size() + 1) < command_panel_height) {
					scrollYOffset = 0;
				}
				
				g.setColor(new Color(45, 60, 70, 125));
				g.fillRect(normal_width, (registerFont.getSize() + 5) * (currentCommandFromFile) + scrollYOffset + 5, command_panel_width - normal_width, registerFont.getSize()+5);
				
				g.setColor(new Color(24, 24, 24));
				g.fillRect(normal_width, 0, paddingLeft+4, command_panel_height);
				
				for(int i = 0; i < commandsFromFile.size(); i++) {
					g.setColor(new Color(155, 155, 155));
					g.drawString((commandsFromFile.get(i).getLine()+1)+"", normal_width + 2, (registerFont.getSize() + 5) * (i+1) + scrollYOffset);
					
					int textWidth = g.getFontMetrics().stringWidth(commandsFromFile.get(i).getName());
					if(textWidth > maxTextWidth) {
						maxTextWidth = textWidth;
					}
					if(textWidth +  normal_width + paddingLeft + 9 + scrollXOffset >= normal_width) {						
						g.drawString(commandsFromFile.get(i).getName(), normal_width + paddingLeft + 9 + scrollXOffset, (registerFont.getSize() + 5) * (i+1) + scrollYOffset);
					}
				}
				
				if(commandsFromFile.size() > currentCommandFromFile) {
					nextLine = commandsFromFile.get(currentCommandFromFile).getLine();
				}
			} else {
				this.CloseFile();
			}
		}
		
		String nextLineValue = Integer.toBinaryString(nextLine);
		
		while(nextLineValue.length() < 8) {
			nextLineValue = "0"+nextLineValue;
		}
		
		g.setColor(new Color(125, 215, 255));
		g.fillRect(10, 10, g.getFontMetrics().stringWidth(nextLineValue) + 10, registerHeight + 10);
		g.setColor(new Color(0, 0, 0));
		g.drawString(nextLineValue, 15,  registerHeight + 12 + 0 * (registerHeight + 20));
		
		int registerX = normal_width - g.getFontMetrics().stringWidth("00000000") - 20;
		int registerY = 10;
		for(int i = 0; i < registers.length; i++) {
			String registerValue = "";
			boolean[] bits = registers[i].ReadValue();
			for(int j = bits.length - 1; j >= 0; j--) {
				registerValue += bits[j] == true ? "1" : '0';
			}
			
//			System.out.println("Register "+i+" has value '"+registerValue+"'");
			
			g.setColor(new Color(125, 215, 255));
			g.fillRect(registerX, registerY + i * (registerHeight + 20), g.getFontMetrics().stringWidth(registerValue) + 10, registerHeight + 10);
			
			g.setColor(new Color(0, 0, 0));
			g.drawString(registerValue, 5 + registerX, registerY + registerHeight - 2 + 5 + i * (registerHeight + 20));
		}
		
		g.setColor(new Color(125, 215, 105));
		g.fillRect(registerX, registerY + registers.length * (registerHeight + 20), g.getFontMetrics().stringWidth("0") + 10, registerHeight + 10);
		
		g.setColor(new Color(0, 0, 0));
		g.drawString(comparisonBit[0] == true ? "1" : "0", registerX + 5, registerY + registerHeight - 2 + 5 + registers.length * (registerHeight + 20));
		
		registerX += g.getFontMetrics().stringWidth("0") + 20;
		
		g.setColor(new Color(125, 215, 105));
		g.fillRect(registerX, registerY + registers.length * (registerHeight + 20), g.getFontMetrics().stringWidth("0") + 10, registerHeight + 10);
		
		g.setColor(new Color(0, 0, 0));
		g.drawString(comparisonBit[1] == true ? "1" : "0", registerX + 5, registerY + registerHeight - 2 + 5 + registers.length * (registerHeight + 20));
	}
	
	public boolean OpenFromFile() {
		ProgramCompiler p = new ProgramCompiler();
		commandsFromFile = p.ListOfCommands();
		if(commandsFromFile == null) {
			ComputerTerminal.ErrorOut(ProgramCompiler.GetLastCompilerError());
			return false;
		}
		currentCommandFromFile = 0;
		this.setPreferredSize(new Dimension(command_panel_width, command_panel_height));
		InitFrame();
		memory.SetWriteEnabled(true);
		ExecutingCommandFromFile = false;
		return true;
	}
	
	public void CloseFile() {
		commandsFromFile = null;
		currentCommandFromFile = -1;
		this.setPreferredSize(new Dimension(normal_width, normal_height));
		memory.SetWriteEnabled(false);
		ExecutingCommandFromFile = false;
		InitFrame();
	}

	public boolean ExecuteCommand(String commandInput) {
		try {
			boolean temp0 = comparisonBit[0];
			boolean temp1 = comparisonBit[1];
			comparisonBit[0] = false;
			comparisonBit[1] = false;
			if(temp1 == true && temp0 == false) {
				return true;
			}
			String formattedInput = "";
			boolean removeSpace = true;
			for(int i = 0; i < commandInput.length(); i++) {
				char a = commandInput.charAt(i);
				if((int)a != 32) {
					formattedInput += a;
					if(a == '"') {
						removeSpace = !removeSpace;
					}
				} else {
					if(!formattedInput.substring(formattedInput.length()-1).equals(" ") && removeSpace == true) {
						formattedInput += " ";
					} else if(removeSpace == false) {
						formattedInput += " ";
					}
				}
			}
			commandInput = formattedInput;
			
			String command = "";
			for(int i = 0; i <= commandInput.length(); i++) {
				int code = 32;				
				if(i < commandInput.length()) {
					code = commandInput.charAt(i);				
				}
				
				if(code == 59) { break; }
				
				if(code != 32) { command += (char)code; }
				else {
					String commandParameterEval;
					
					int parameterIndex = 0;
					int[] paratemers = new int[2];
					int[] types = new int[2];
					String parameter = "";
					switch(command) {
					case "move":
						commandParameterEval = commandInput.substring(i+1);
						paratemers = new int[2];
						types = new int[2];
						parameter = "";
						for(int j = 0, parameterCount = 0; j <= commandParameterEval.length(); j++) {
							int parameterChar = 32;
							if(j < commandParameterEval.length()) {
								parameterChar = commandParameterEval.charAt(j);
							}
							
							if(parameterChar != 32) { parameter += (char)parameterChar; }
							else {
								if(parameterCount == 0 || parameterCount == 2) {								
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, parameterCount == 2 ? 28 : 31, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								}
								parameterCount++;
								parameter = "";
							}
						}
						
						System.out.println("type[0]="+types[0]);
						System.out.println("type[1]="+types[1]);
						
						boolean canReturn = true;
						if(types[0] == 1) {
							if(types[1] == 4) {
								memory.WriteValue(paratemers[0], paratemers[1]);
							} else if(types[1] == 16) {
								WriteToRegister(paratemers[0], paratemers[1]-1);
							} else {
								canReturn = false;
							}
						} else if(types[0] == 4) {
							if(types[1] == 4) {
								System.out.println("Trying to write value at address '"+paratemers[0]+"' to '"+paratemers[1]+"'");
								memory.WriteValue(memory.ReadValue(paratemers[0]), paratemers[1]);
								memory.WriteValue(0, paratemers[0]);
							} else if(types[1] == 16) {
								int oldValue = memory.ReadValue(paratemers[0]);
								WriteToRegister(oldValue, paratemers[1]-1);
								memory.WriteValue(0, paratemers[0]);
							} else {
								canReturn = false;
							}
						} else if(types[0] == 16) {
							if(types[1] == 4) {
								int value = ReadRegister(paratemers[0]-1);
								memory.WriteValue(value, paratemers[1]);
								WriteToRegister(0, paratemers[0]-1);
							} else if(types[1] == 16) {
								boolean[] bits = registers[paratemers[0]-1].ReadValue();
								
								registers[paratemers[1]-1].WriteValue(bits);
								registers[paratemers[0]-1].WriteValue(new boolean[bits.length]);
							} else {
								canReturn = false;
							}
						} else {
							canReturn = false;
						}
						
						if(canReturn) {
							return true;
						}
					break;
					case "copia":
						commandParameterEval = commandInput.substring(i+1);
						paratemers = new int[2];
						types = new int[2];
						parameter = "";
						for(int j = 0, parameterCount = 0; j <= commandParameterEval.length(); j++) {
							int parameterChar = 32;
							if(j < commandParameterEval.length()) {
								parameterChar = commandParameterEval.charAt(j);
							}
							
							if(parameterChar != 32) { parameter += (char)parameterChar; }
							else {
								if(parameterCount == 0 || parameterCount == 2) {								
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, 28, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								}
								parameterCount++;
								parameter = "";
							}
						}
						
						System.out.println("type[0]="+types[0]);
						System.out.println("type[1]="+types[1]);
						
						canReturn = true;
						if(types[0] == 1) {
							if(types[1] == 4) {
								memory.WriteValue(paratemers[0], paratemers[1]);
							} else if(types[1] == 16) {
								WriteToRegister(paratemers[0], paratemers[1]-1);
							} else {
								canReturn = false;
							}
						} else if(types[0] == 4) {
							if(types[1] == 4) {
								System.out.println("Trying to write value at address '"+paratemers[0]+"' to '"+paratemers[1]+"'");
								memory.WriteValue(memory.ReadValue(paratemers[0]), paratemers[1]);
							} else if(types[1] == 16) {
								int oldValue = memory.ReadValue(paratemers[0]);
								WriteToRegister(oldValue, paratemers[1]-1);
							} else {
								canReturn = false;
							}
						} else if(types[0] == 16) {
							if(types[1] == 4) {
								int value = ReadRegister(paratemers[0]-1);
								memory.WriteValue(value, paratemers[1]);
							} else if(types[1] == 16) {
								boolean[] bits = registers[paratemers[0]-1].ReadValue();
								
								registers[paratemers[1]-1].WriteValue(bits);
								registers[paratemers[0]-1].WriteValue(new boolean[bits.length]);
							} else {
								canReturn = false;
							}
						} else {
							canReturn = false;
						}
						
						if(canReturn) {
							return true;
						}
					break;
					case "add":
						commandParameterEval = commandInput.substring(i+1);
						paratemers = new int[2];
						types = new int[2];
						parameter = "";
						for(int j = 0, parameterCount = 0; j <= commandParameterEval.length(); j++) {
							int parameterChar = 32;
							if(j < commandParameterEval.length()) {
								parameterChar = commandParameterEval.charAt(j);
							}
							
							if(parameterChar != 32) { parameter += (char)parameterChar; }
							else {
								if(parameterCount == 0) {								
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, 21, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								} else {
									throw new CompilerSyntaxException("Too many parameters for command.");
								}
								parameterCount++;
								parameter = "";
							}
						}
						
						boolean canWriteNewValue = true;
						int addValue = ReadRegister(0);
						
						if(types[0] == 1) {
							addValue += paratemers[0];
						} else if(types[0] == 4) {
							addValue += memory.ReadValue(paratemers[0]);
						} else if(types[0] == 16) {
							addValue += ReadRegister(paratemers[0]-1);
						} else {
							canWriteNewValue = false;
						}
						
						if(canWriteNewValue) {
							WriteToRegister(addValue, 0);
							return true;
						}
					break;
					case "sub":
						commandParameterEval = commandInput.substring(i+1);
						paratemers = new int[2];
						types = new int[2];
						parameter = "";
						for(int j = 0, parameterCount = 0; j <= commandParameterEval.length(); j++) {
							int parameterChar = 32;
							if(j < commandParameterEval.length()) {
								parameterChar = commandParameterEval.charAt(j);
							}
							
							if(parameterChar != 32) { parameter += (char)parameterChar; }
							else {
								if(parameterCount == 0) {								
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, 21, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								} else {
									throw new CompilerSyntaxException("Too many parameters for command.");
								}
								parameterCount++;
								parameter = "";
							}
						}
						
						canWriteNewValue = true;
						addValue = ReadRegister(0);
						
						if(types[0] == 1) {
							addValue -= paratemers[0];
						} else if(types[0] == 4) {
							addValue -= memory.ReadValue(paratemers[0]);
						} else if(types[0] == 16) {
							addValue -= ReadRegister(paratemers[0]-1);
						} else {
							canWriteNewValue = false;
						}
						
						if(canWriteNewValue) {
							WriteToRegister(addValue, 0);
							return true;
						}
					break;
					case "pula":
						if(!ExecutingCommandFromFile) {
							throw new CompilerSyntaxException("Command 'pula' was executed from terminal, prohibited");
						}
						commandParameterEval = commandInput.substring(i+1);
						paratemers = new int[1];
						types = new int[1];
						parameter = "";
						for(int j = 0, parameterCount = 0; j <= commandParameterEval.length(); j++) {
							int parameterChar = 32;
							if(j < commandParameterEval.length()) {
								parameterChar = commandParameterEval.charAt(j);
							}
							
							if(parameterChar != 32) { parameter += (char)parameterChar; }
							else {
								if(parameterCount == 0) {								
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, 1, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								} else {
									throw new CompilerSyntaxException("Command 'pula' expects only one parameter");
								}
								parameterCount++;
								parameter = "";
							}
						}
						
						System.out.println("type[0]="+types[0]);
						
						canReturn = true;
						if(types[0] == 1) {
							int jumpLineIndex = -1;
							for(int cff = 0; cff < this.commandsFromFile.size(); cff++) {
								if(paratemers[0] == commandsFromFile.get(cff).getLine()+1) {
									System.out.println(paratemers[0]+ "==" + commandsFromFile.get(cff).getLine());
									System.out.println(commandsFromFile.get(cff).getName());
									jumpLineIndex = cff;
								}
							}
							if(jumpLineIndex == -1) {
								throw new CompilerSyntaxException("Was not possible to jump to line "+paratemers[0]);
							}
							currentCommandFromFile = jumpLineIndex-1;
						}
						
						if(canReturn) {
							return true;
						}
					break;
					case "comp":
						commandParameterEval = commandInput.substring(i+1);
						paratemers = new int[3];
						types = new int[3];
						parameter = "";
						for(int j = 0, parameterCount = 0; j <= commandParameterEval.length(); j++) {
							int parameterChar = 32;
							if(j < commandParameterEval.length()) {
								parameterChar = commandParameterEval.charAt(j);
							}
							
							if(parameterChar != 32) { parameter += (char)parameterChar; }
							else {
								if(parameterCount == 0 || parameterCount == 2) {								
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, 29, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								} else if(parameterCount == 1) {
									System.out.println("paramter: "+parameter);
									if(ProgramCompiler.CheckCommandParameter(parameter, 64, 0)) {
										int parameterType = ProgramCompiler.GetCommandParameterType(parameter);
										int parameterValue = ProgramCompiler.GetCommandParameterValue(parameter);
										
										System.out.println("\ttype: "+parameterType);
										
										paratemers[parameterIndex] = parameterValue;
										types[parameterIndex] = parameterType;
										parameterIndex++;
									} else {
										this.commandError = ProgramCompiler.GetLastCompilerError();
										return false;
									}
								}
								parameterCount++;
								parameter = "";
							}
						}
						
						for(int tp = 0; tp < types.length; tp++) {
							System.out.println("type["+tp+"]="+types[tp]+"\tparameter["+tp+"]="+paratemers[tp]);
						}
						
						int val1 = 0;
						int val2 = 0;
						
						canReturn = true;
						if(types[0] == 1) {
							val1 = paratemers[0];
						} else if(types[0] == 4) {
							val1 = memory.ReadValue(paratemers[0]);
						} else if(types[0] == 16) {
							val1 = ReadRegister(paratemers[0]-1);
						} else {
							canReturn = false;
						}
						
						if(types[2] == 1) {
							val2 = paratemers[2];
						} else if(types[2] == 4) {
							val2 = memory.ReadValue(paratemers[2]);
						} else if(types[2] == 16) {
							val2 = ReadRegister(paratemers[2]-1);
						} else {
							canReturn = false;
						}
						
						System.out.println("val1= "+val1+"\tval2= "+val2);
						
						if(canReturn) {
							comparisonBit[1] = true;
							switch(paratemers[1]) {
							case 0: comparisonBit[0] = (val1 > val2); break;
							case 1: comparisonBit[0] = (val1 < val2); break;
							case 2: comparisonBit[0] = (val1 >= val2); break;
							case 3: comparisonBit[0] = (val1 <= val2); break;
							case 4: comparisonBit[0] = (val1 == val2); break;
							case 5: comparisonBit[0] = (val1 != val2); break;
							default: comparisonBit[0] = false;
							}
							return true;
						}
					break;
					case "/writeenable":
						memory.ToggleWriteEnabled();
						return true;
					case "/openfile":
						if(!OpenFromFile()) {
							throw new CompilerSyntaxException("Compiler was not able to open file.");
						}
						return true;
					case "/closefile":
						CloseFile();
						return true;
					default:
						throw new CompilerSyntaxException("Command '"+command+"' was not recognized.");
					}
					
					command = "";
				}
			}
			
			return true;
		} catch(CompilerSyntaxException e){
			e.printStackTrace();
			commandError = e.getMessage();
			return false;
		}
	}
	
	public String LastErrorMessage() {
		return this.commandError;
	}
	
	public void WriteToRegister(int value, int registerIndex) {
		if(registerIndex < 0 || registerIndex > registers.length - 1) {
			return;
		}
		String v = Integer.toBinaryString(value);
		while(v.length() < 8) {
			v = "0"+v;
		}
		
		if(v.length() > 8) {
			System.err.println("Value bigger than byte");
			return;
		}
		
		boolean[] bits = new boolean[8];
		
		for(int i = 0; i < v.length(); i++) {
			bits[bits.length - i - 1] = v.charAt(i) == '1';
		}
		
		registers[registerIndex].WriteValue(bits);
		
		System.out.println("Written "+value+" to register "+registerIndex);
	}
	
	public int ReadRegister(int registerIndex) {
		if(registerIndex > registers.length - 1 || registerIndex < 0) {
			System.err.println("Register index out of bounds");
			return 0;
		}
		
		boolean[] bits = registers[registerIndex].ReadValue();
		
		int returnValue = 0;
		
		for(int i = 0; i < bits.length; i++) {
			if(bits[i] == true) {
				int multi = 1;
				for(int j = 0; j < i; j++) {
					multi *= 2;
				}
				returnValue += multi;
			}
		}
		
		return returnValue;
	}
}


