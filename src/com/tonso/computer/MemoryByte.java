package com.tonso.computer;

public class MemoryByte {
	private boolean[] bits = new boolean[8];
	
	public MemoryByte() {
		for(int i = 0; i < bits.length; i++) {
			bits[i] = false;
		}
	}
	
	public boolean WriteValue(boolean[] value) {
		if(value.length > bits.length) {
			System.err.println("tried to write value bigger than byte size");
			return false;
		} else if(value.length < bits.length) {
			System.out.println("value smaller");
			boolean[] oldValue = new boolean[value.length];
			
			System.out.print("Old value: ");
			for(int i = 0; i < oldValue.length; i++) {
				oldValue[i] = value[i];
				System.out.print(oldValue[i] == true ? '1' : '0');
			}
			
			System.out.println();
			
			value = new boolean[bits.length];
			
			int sizeDiff = value.length - oldValue.length;
			
			System.out.print("New value: ");
			for(int i = 0; i < value.length; i++) {
				if(i < sizeDiff) {					
					value[i] = false;
				} else {
					value[i] = oldValue[i - sizeDiff];
				}
				System.out.print(value[i] == true ? '1' : '0');
			}
			System.out.println();
		}
		
		bits = value;
		
		return true;
	}
	
	public boolean[] ReadValue() {
		return bits;
	}
}
