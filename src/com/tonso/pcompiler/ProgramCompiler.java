package com.tonso.pcompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;



public class ProgramCompiler {
	
	public static final String[] recognizedCommands = new String[] { "move", "add", "sub", "pula", "comp" };
	public static final String[] comparisonChars = new String[] { ">", "<", ">=", "<=", "=", "!=" };
	public static final String forbiddenSectionsChar = ".!\"'#$%¨&*()_+-={}[]´`^~?/°|\\@§/?";
	
	private static String LastCompilerError = "";
	
	private List<CompilerLine> commands;
	
	public static String GetLastCompilerError() {
		return ProgramCompiler.LastCompilerError;
	}
	
	public List<CompilerLine> ListOfCommands() {
		return commands;
	}
	
	public ProgramCompiler() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			ProgramCompiler.LastCompilerError = e.getMessage();
			this.commands = null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			ProgramCompiler.LastCompilerError = e.getMessage();
			this.commands = null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			ProgramCompiler.LastCompilerError = e.getMessage();
			this.commands = null;
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
			ProgramCompiler.LastCompilerError = e.getMessage();
			this.commands = null;
		}
		
		JFileChooser chooser = new JFileChooser();
        int status = chooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }
            
            String filename = file.getName();
			int fN = filename.lastIndexOf('.');
			
			if(fN == -1) {
				return;
			}
            
            String fileExtension = filename.substring(fN+1).toLowerCase();
            
            if(!fileExtension.equals("n")) {
            	return;
            }
            
            String path = file.getAbsolutePath();
            String separator = File.separator;
            System.out.println(separator);
            String compare = "";
            String newPath = "";
            for(int j = path.length() - 1; j >= 0; j--) {
            	if(!newPath.equals("")) {
            		if(newPath.equals("\n")) {
            			newPath = "";
            		}
            		newPath = path.charAt(j) + newPath;
            	} else {            		
            		compare = path.charAt(j) + compare;
            		
            		if(compare.length() + 1 > separator.length()) {
            			char[] c = compare.toCharArray();
            			compare = "";
            			for(int k = 0; k < c.length && k < separator.length(); k++) {
            				compare = compare + c[k];
            			}
            		}
            		
            		if(compare.equals(separator)) {
            			newPath = "\n";
            		}
            		
            		System.out.println(compare);
            	}
            }
            
            System.out.println(newPath);
            
            try {
            	Scanner SectionFinder = new Scanner(file);
            	commands = new ArrayList<CompilerLine>();
            	
            	int LineIndex = -1;
            	while(SectionFinder.hasNextLine()) {
            		LineIndex++;
            		String line = SectionFinder.nextLine();
            		
            		String command = "";
            		for(int i = 0; i <= line.length(); i++) {
            			char c = 32;
            			if(i < line.length()) {
            				c = line.charAt(i);
            			}
            			
            			if(c == 59) { break; } // Found comment
            			else if(c != 32) { command += c; }
            			else if(c == 32 && !command.equals("")) {
            				System.out.println(LineIndex +": "+command);
            				boolean satisfiedConditions = false;
							List<String> commandParameters = new ArrayList<String>();
							String commandParameter = "";
							String[] parametersFound = new String[0];
							CompilerCommandParameter[] parameters = new CompilerCommandParameter[0];
							
							switch(command) {
							case "move":
								parameters = new CompilerCommandParameter[] {
										new CompilerCommandParameter(31, LineIndex),
										new CompilerCommandParameter("para", LineIndex),
										new CompilerCommandParameter(28, LineIndex)
								};
							break;
							case "copia":
								System.out.println("Commando copia encontrado");
								parameters = new CompilerCommandParameter[] {
										new CompilerCommandParameter(28, LineIndex),
										new CompilerCommandParameter("para", LineIndex),
										new CompilerCommandParameter(28, LineIndex)
								};
							break;
							case "add":
								parameters = new CompilerCommandParameter[] {
										new CompilerCommandParameter(29, LineIndex)
								};
							break;
							case "sub":
								parameters = new CompilerCommandParameter[] {
										new CompilerCommandParameter(29, LineIndex)
								};
							break;
							case "pula":
								parameters = new CompilerCommandParameter[] {
										new CompilerCommandParameter(1, LineIndex)
								};
							break;
							case "comp":
								System.out.println("comp command found");
								parameters = new CompilerCommandParameter[] {
										new CompilerCommandParameter(29, LineIndex),
										new CompilerCommandParameter(64, LineIndex),
										new CompilerCommandParameter(29, LineIndex)
								};
							break;
							default:
								if(!commandParameter.equals("")) {
									System.out.println("Checking for variable at '"+commandParameter+"' line: "+LineIndex);
									if(!CheckCommandParameter(commandParameter, 8, LineIndex)) {    									
										satisfiedConditions = false;
										throw new Exception("Command '"+command+"' is not a valid command.");
									} else {
										commandParameters.add(commandParameter);
										System.out.println("Added command");
									}
								}
							}
							
							parametersFound = EvaluateParameters(line.substring(command.length()+1), parameters, LineIndex);
							String commandToAdd = command;
							System.out.println("parameters: "+parameters.length);
							if(parametersFound != null) {
								System.out.println("found "+parametersFound.length+" parameters");
								satisfiedConditions = true;
								for(int parameterFoundIndex = 0; parameterFoundIndex < parametersFound.length; parameterFoundIndex++) {
									System.out.println("\tcommandToAdd= "+commandToAdd);
									commandToAdd += " "+parametersFound[parameterFoundIndex];
								}
							}
														
							if(satisfiedConditions == true) {
								commands.add(new CompilerLine(commandToAdd, LineIndex));
								break;
							}
							
							command = "";
            			}
            		}
            	}
            	
            	SectionFinder.close();
            	
            	for(CompilerLine cLineVal : commands) {
        			System.out.println(cLineVal.getLine()+": "+cLineVal.getName());
        		}
			} catch (FileNotFoundException e) {
//				e.printStackTrace();
				ProgramCompiler.LastCompilerError = e.getMessage();
				this.commands = null;
			} catch (CompilerSyntaxException e) {
//				e.printStackTrace();
				ProgramCompiler.LastCompilerError = e.getMessage();
				this.commands = null;
			} catch (Exception e) {
//				e.printStackTrace();
				ProgramCompiler.LastCompilerError = e.getMessage();
				this.commands = null;
			}
        }
	}
	
	public static String[] EvaluateParameters(String line, CompilerCommandParameter[] parameters, int LineIndex) {
		try {
			System.out.println("Evaluating '"+line+"'");
			int currentParameter = 0;
			String commandParameter = "";
			String[] returnParameters = new String[parameters.length];
			for(int i = 0; i <= line.length(); i++) {
				char cp = 32;
				
				if(i < line.length()) {
					cp = line.charAt(i);
				}
				
				if(cp == 59) { break; } // Found comment
				else if(cp != 32) {
					commandParameter += cp;
				} else {
					if(currentParameter < parameters.length && !commandParameter.equals("")) {
						System.out.println("\tparameter '"+commandParameter+"' found, evaluating");
						CompilerCommandParameter parameter = parameters[currentParameter];
						if(parameter.Type() != -1) {
							System.out.println("\tParameter type is not -1, being "+parameter.Type());
							if(ProgramCompiler.CheckCommandParameter(commandParameter, parameter.Type(), LineIndex)){
								System.out.println("\tAdding to return parameters array, '"+commandParameter+"'");
								returnParameters[currentParameter] = commandParameter;
							} else {
								throw new Exception("Command at line "+LineIndex+" is not a valid command");
							}
						} else if(!parameter.DefaultValue().equals("")) {
							if(parameter.DefaultValue().equals(commandParameter)) {								
								returnParameters[currentParameter] = commandParameter;
							} else {
								throw new Exception("Expected keyword '"+parameter.DefaultValue()+"'at line "+LineIndex);
							}
						} else {
							throw new Exception("wtf?");
						}
					} else {
						throw new Exception("Command at line "+LineIndex+" accepts only "+parameters.length+" parameters");
					}
					
					currentParameter++;
					commandParameter = "";
				}
			}	
			return returnParameters;
		} catch (Exception e) {
//			e.printStackTrace();
			ProgramCompiler.LastCompilerError = e.getMessage();
			return null;
		}
	}
	
	public static int GetCommandParameterValue(String commandParameter) {
		int comparison = -1;
		for(int comp = 0; comp < comparisonChars.length; comp++) {
			if(commandParameter.equals(comparisonChars[comp])) {
				comparison = comp;
				break;
			}
		}
		
		if(comparison != -1) {
			return comparison;
		} else if(CheckSyntax(commandParameter, "[", "]") == true && (commandParameter.indexOf("[") != -1 || commandParameter.indexOf("]") != -1)) {
			int StartIndex = commandParameter.indexOf("[");
			int EndIndex = commandParameter.indexOf("]");
			
			String value = commandParameter.substring(StartIndex+1, EndIndex);
			
			boolean[] bits = ConvertToBinary(value, "0123456789abcdef");
			
			int integerValue = 0;
			for(int i = 0; i < bits.length; i++) {
				if(bits[i] == true) {
					int multi = 1;
					for(int j = 0; j < i; j++) {
						multi *= 2;
					}
					integerValue += multi;
				}
			}
			return integerValue;
		} else if(CheckSyntax(commandParameter, "(", ")") == true && (commandParameter.indexOf("(") != -1 || commandParameter.indexOf(")") != -1)) {
			int StartIndex = commandParameter.indexOf("(");
			int EndIndex = commandParameter.indexOf(")");
			
			String value = commandParameter.substring(StartIndex+1, EndIndex);
			
			boolean[] bits = ConvertToBinary(value, "0123456789");
			
			int integerValue = 0;
			for(int i = 0; i < bits.length; i++) {
				if(bits[i] == true) {
					int multi = 1;
					for(int j = 0; j < i; j++) {
						multi *= 2;
					}
					integerValue += multi;
				}
			}
			return integerValue;
		} else if (commandParameter.indexOf('"') != -1) {
			return 0;
		} else {
			if(commandParameter.substring(0, 1).equals("&")) {
				return 8;
			} else if(commandParameter.substring(0, 1).equals("#")) {
				String registerName = commandParameter.substring(1);
				
				switch(registerName) {
				case "atx": return 1;
				case "etx": return 2;
				case "itx": return 3;
				case "otx": return 4;
				default: return 0;
				}
			} else {
				boolean[] bits = ConvertToBinary(commandParameter, "0123456789");
				
				int integerValue = 0;
				for(int i = 0; i < bits.length; i++) {
					if(bits[i] == true) {
						int multi = 1;
						for(int j = 0; j < i; j++) {
							multi *= 2;
						}
						integerValue += multi;
					}
				}
				return integerValue;
			}
		}
	}
	
	public static byte GetCommandParameterType(String commandParameter) {
		if(CheckSyntax(commandParameter, "[", "]") == true && (commandParameter.indexOf("[") != -1 || commandParameter.indexOf("]") != -1)) {
			return 4;
		} else if(CheckSyntax(commandParameter, "(", ")") == true && (commandParameter.indexOf("(") != -1 || commandParameter.indexOf(")") != -1)) {
			return 4;
		} else if (commandParameter.indexOf('"') != -1) {
			return 2;
		} else {
			if(commandParameter.substring(0, 1).equals("&")) {
				return 8;
			} else if(commandParameter.substring(0, 1).equals("#")) {
				return 16;
			} else {
				for(int i = 0; i < comparisonChars.length; i++) {
					if(commandParameter.equals(comparisonChars[i])){
						System.out.println("Comparator "+commandParameter+" found");
						return 64;
					}
				}
				return 1;
			}
		}
	}
	
	public static boolean CheckCommandParameter(String commandParameter, int type, int LineIndex) {
		boolean satisfiedConditions = true;
		System.out.println("CheckCommandParameter called for '"+commandParameter+"'");
		
		String typeB = Integer.toBinaryString(type);
		
//		index - name | all binary 000010 = only variavel
//		0 - inteiro - valor_decimal, $valor_hexa | 0000001 | 1
//		1 - string - sempre entre '' | 0000010 | 2
//		2 - endereco_memoria - (decimal), [hexa_decimal] | 0000100 | 4
//		3 - variavel - &nome | 0001000 | 8
//		4 - registrador - #register_name | atx, etx, itx, otx | 0010000 | 16
//		5 - function call - .function_name | 0100000 | 32
//		6 - comparison - comp val (cc) val | 1000000 | 64
		
		final String types = "0000000";
		final int typesLength = types.length();
		
		while(typeB.length() < types.length()) {
			typeB = "0"+typeB;
		}
		
		if(typeB.length() > types.length()) {
			System.out.println("length bigger");
			return false;
		}
		
		try {
			if(CheckSyntax(commandParameter, '"', '"') == false || CheckSyntax(commandParameter, "[", "]") == false || CheckSyntax(commandParameter, "(", ")") == false) {                    										
				throw new CompilerSyntaxException("Syntax error in line "+(LineIndex+1));
			} else {
				if(CheckSyntax(commandParameter, "[", "]") == true && (commandParameter.indexOf("[") != -1 || commandParameter.indexOf("]") != -1)) {
					if(typeB.charAt(typesLength - 3) == '0') {
						throw new InvalidCommandException("Command does not support 'memory address' parameters on line "+(LineIndex+1));
					}
					int StartIndex = commandParameter.indexOf("[");
					int EndIndex = commandParameter.indexOf("]");
					
					String value = commandParameter.substring(StartIndex+1, EndIndex);
					
					boolean[] bits = ConvertToBinary(value, "0123456789abcdef");
					
					if(bits == null) {
						throw new NumberFormatException("Erro on line "+LineIndex+"\n\tWas not possible to convert hex value.");
					}
				} else if(CheckSyntax(commandParameter, "(", ")") == true && (commandParameter.indexOf("(") != -1 || commandParameter.indexOf(")") != -1)) {
					if(typeB.charAt(typesLength - 3) == '0') {
						throw new InvalidCommandException("Command does not support 'memory address' parameters on line "+(LineIndex+1));
					}
					int StartIndex = commandParameter.indexOf("(");
					int EndIndex = commandParameter.indexOf(")");
					
					String value = commandParameter.substring(StartIndex+1, EndIndex);
					
					boolean[] bits = ConvertToBinary(value, "0123456789");
					
					if(bits == null) {
						throw new NumberFormatException("Erro on line "+LineIndex+"\n\tWas not possible to convert decimal value.");
					}
				} else if (commandParameter.indexOf('"') != -1) {
					if(typeB.charAt(typesLength - 2) == '0') {
						throw new InvalidCommandException("Command does not support 'string' parameters on line "+(LineIndex+1));
					}
				} else {
					if(commandParameter.length() == 0) {
						throw new InvalidCommandException("Unkown error on line "+(LineIndex+1));
					}
					boolean comparison = false;
					for(int comp = 0; comp < comparisonChars.length; comp++) {
						System.out.println("checking '"+commandParameter+"' with '"+comparisonChars[comp]+"'");
						if(commandParameter.equals(comparisonChars[comp])) {
							comparison = true;
							break;
						}
					}
					
					if(comparison == true) {
						System.out.println("it is a comparator");
						if(typeB.charAt(typesLength - 7) == '0') {
							throw new InvalidCommandException("Command does not support 'caomparators' parameters on line "+(LineIndex+1));
						}
					} else if(commandParameter.substring(0, 1).equals("&")) {
						if(typeB.charAt(typesLength - 4) == '0') {
							throw new InvalidCommandException("Command does not support 'variable' parameters on line "+(LineIndex+1));
						}
					} else if(commandParameter.substring(0, 1).equals("#")) {
						if(typeB.charAt(typesLength - 5) == '0') {
							throw new InvalidCommandException("Command does not support 'register' parameters on line "+(LineIndex+1));
						}
					} else if(commandParameter.substring(0, 1).equals(".")) {
						if(typeB.charAt(typesLength - 6) == '0') {
							throw new InvalidCommandException("Command does not support 'function call' parameters on line "+(LineIndex+1));
						}
					} else {
						if(typeB.charAt(typesLength - 1) == '0') {
							throw new InvalidCommandException("Command does not support 'int' parameters on line "+(LineIndex+1));
						}
						
						boolean[] bits = ConvertToBinary(commandParameter, "0123456789");
						
						if(bits == null) {
							throw new NumberFormatException("Erro on line "+LineIndex+"\n\tWas not possible to convert decimal value.");
						}
					}
				}
				return true;
			}
		} catch (CompilerSyntaxException e) {
//			e.printStackTrace();
			satisfiedConditions = false;
			ProgramCompiler.LastCompilerError = e.getMessage();
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			satisfiedConditions = false;
			ProgramCompiler.LastCompilerError = e.getMessage();
		} catch (InvalidCommandException e) {
//			e.printStackTrace();
			satisfiedConditions = false;
			ProgramCompiler.LastCompilerError = e.getMessage();
		}
		
		return satisfiedConditions;
	}
	
	public static boolean[] ConvertToBinary(String value, String valuesSequence) {
		int parameterValue = 0;
		for(int p = value.length()-1; p >= 0; p--) {
			char pChar = value.charAt(p);
			int pIndex = valuesSequence.indexOf(pChar);
			
			if(pIndex != -1) {
				int hexMult = 1;
				for(int hMult = value.length()-1; hMult > p; hMult--) {
					hexMult *= valuesSequence.length();
				}
				parameterValue += pIndex * hexMult;
			} else {
				return null;
			}
		}
		
		String binary = Integer.toBinaryString(parameterValue);
		
		System.out.println("binary value="+binary);
		
		boolean[] bits = new boolean[binary.length()];
		
		for(int i = bits.length-1; i >= 0; i--) {
			bits[bits.length - i - 1] = (binary.charAt(i) == '1');  
		}
		
		return bits;
	}
	
	public static boolean CheckSyntax(String commandParameter, String character1, String character2) {
		int compareStartIndex = commandParameter.indexOf(character1);
		int compareEndIndex = commandParameter.lastIndexOf(character2);
		
		int compareValue = (compareStartIndex != -1 ? 1 : 0) + (compareEndIndex != -1 ? 1 : 0);
		
		return !((compareValue != 2 && compareValue != 0) || (compareStartIndex == compareEndIndex && compareValue != 0));
	}
	
	public static boolean CheckSyntax(String commandParameter, char character1, char character2) {
		return CheckSyntax(commandParameter, ""+character1, ""+character2);
	}
}
