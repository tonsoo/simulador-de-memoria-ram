package com.tonso.computer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;

import com.tonso.graphics.Screen;

public class Memory extends Screen {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3599376236133262392L;
	
	private MemoryByte[] bytes;
	private boolean memoryError = false;
	
	private Font byteFont;
	private Font uiFont;
	
	private int scrollYOffset=0, scrollIncreaseTimeout=90, scrollAux=0, scrollMultiplier=1;
	
	private boolean DraggingScrollBar = false;
	
	private int maxSize = 0, memoryWidth = 0;
	private int scrollBarHeight=0, lastByteY=0;
	
	private boolean WriteEnabled = false;
	
	private String lastError = "";
	
	public Memory(int width, int height, int memoryPower) {
		super(width, height, "Memory Panel", JFrame.EXIT_ON_CLOSE);
		
		int size = 1;
		
		for(int i = 0; i < memoryPower; i++) {
			size *= 2;
		}
		
		bytes = new MemoryByte[size];
		
		for(int i = 0; i < size; i++) {
			bytes[i] = new MemoryByte();
		}
		
		System.out.println("Total memory size: "+size);
		
		byteFont = new Font("Arial", Font.PLAIN, 26);
		uiFont = new Font("Arial", Font.PLAIN, 16);
	}
	
	private void setError(String message) {
		memoryError = true;
		lastError = message;
	}
	
	public void WriteValue(int value, int address) {
		if(!WriteEnabled) {
			setError("Write not enabled");
			return;
		}
		String v = Integer.toBinaryString(value);
		while(v.length() < 8) {
			v = "0"+v;
		}
		
		if(v.length() > 8) {
			System.err.println("Value bigger than byte");
			setError("Byte length excceded");
			return;
		}
		
		if(address >= bytes.length || address < 0) {
			System.err.println("Memory address overflow");
			setError("Memory address overflow");
			return;
		}
		
		boolean[] bits = new boolean[8];
		
		for(int i = 0; i < v.length(); i++) {
			bits[bits.length - i - 1] = v.charAt(i) == '1';
		}
		
		bytes[address].WriteValue(bits);
		
		System.out.println("Written "+value+" to address "+address);
		memoryError = false;
	}
	
	public int ReadValue(int address) {
		if(address >= bytes.length || address < 0) {
			System.err.println("Memory address overflow");
			setError("Memory address overflow");
			return 0;
		}
		
		boolean[] bits = bytes[address].ReadValue();
		
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
		
		memoryError = false;
		return returnValue;
	}
	
	@Override
	public void FixedUpdate() {
		if(scrollAux <= scrollIncreaseTimeout) {			
			scrollAux++;
		}
	}

	@Override
	public void Render(Graphics g) {
		g.setColor(new Color(200,230,200));
		g.fillRect(0, 0, memoryWidth, height);
		
		g.setColor(new Color(0,0,0));
		g.setFont(byteFont);
		
		int ScrollBarWidth = 0;
		
		lastByteY = 10 * (bytes.length + 1) + bytes.length * 30 - height;
		
		if(lastByteY > height) {
			ScrollBarWidth = 10;
			
			if(scrollYOffset > 0) {
				scrollYOffset = 0;
			} else if(scrollYOffset < -lastByteY) {
				scrollYOffset = -lastByteY;
			}
			
			scrollBarHeight = (int)((double)height / ((double)lastByteY / (double)height));
			if((double)scrollBarHeight / (double)height < 0.1) {
				scrollBarHeight = (int)((double)height * 0.1);
			}
		} else {
			scrollYOffset = 0;
		}
		
		maxSize = Integer.toHexString(bytes.length).length() - 1;
		memoryWidth = (maxSize + 8) * 2 * 10 + 10;
		
		int usage = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			int offset = 10 * (i + 1) + i * 30 + scrollYOffset;
			
			if(offset > height || offset < 0 - 30) {
				continue;
			}
			g.setColor(new Color(240, 255, 240));
			g.fillRect(10, offset, memoryWidth-ScrollBarWidth - 20, 30);
			
			boolean[] bits = bytes[i].ReadValue();
			
			String v = "";
			for(int j = bits.length-1; j >= 0; j--) {
				v += bits[j] == true ? '1' : '0';
			}
			
			g.setColor(new Color(0, 0, 0));
			g.drawString(v, memoryWidth - 14*v.length()-20-ScrollBarWidth, 25 + offset);
			
			String address = Integer.toHexString(i);
			
			while(address.length() < maxSize) {
				address = "0"+address;
			}
			
			g.drawString(address, 20, 25 + offset);
		}
		
		g.setColor(new Color(42, 42, 42));
		g.fillRect(memoryWidth - ScrollBarWidth, (int)(((double)scrollYOffset / (double)(lastByteY)) * -(height - scrollBarHeight)), ScrollBarWidth, scrollBarHeight);
		
		g.setFont(uiFont);
		
		int RenderX = memoryWidth + 20;
		
		String writeEnabled = "Write Enabled";
		
		g.setColor(new Color(0, 0, 0));
		g.drawString(writeEnabled, RenderX, 24);
		
		if(WriteEnabled == false) {			
			g.setColor(new Color(200, 25, 35));
		} else {			
			g.setColor(new Color(25, 200, 35));
		}
		g.fillRect(RenderX, 38, 20, 20);
		
		RenderX += g.getFontMetrics().stringWidth(writeEnabled) + 50;
		
		String memoryErrorText = "Error";
		
		g.setColor(new Color(0, 0, 0));
		g.drawString(memoryErrorText, RenderX, 24);
		
		if(memoryError == false) {
			g.setColor(new Color(200, 25, 35));
		} else {
			g.setColor(new Color(25, 200, 35));
		}
		g.fillRect(RenderX, 38, 20, 20);
		
		RenderX += g.getFontMetrics().stringWidth(memoryErrorText) + 50;
		
		String errorLogString = "Last error";
		
		g.setColor(new Color(0, 0, 0));
		g.drawString(errorLogString, RenderX, 25);
		
		g.setColor(new Color(200, 25, 35));
		g.drawString(lastError, RenderX, 50);
		
		String memoryUsage = "Memory Usage";
		
		g.setColor(new Color(0, 0, 0));
		g.drawString(memoryUsage, memoryWidth + 20, 94);
		
		for(int i = 0; i < bytes.length; i++) {
			boolean[] bs = bytes[i].ReadValue();
			for(int j = 0; j < bs.length; j++) {
				if(bs[j] == true) {
					usage++;
					break;
				}
			}
		}
		
		g.drawString(usage+" / "+bytes.length + " bytes", memoryWidth + 20, 114);
		g.drawString((int)((double)usage / (double)bytes.length * 100)+"%", memoryWidth + 20, 132);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getX() < memoryWidth && e.getX() > 0) {			
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
			
			scrollYOffset -= e.getWheelRotation() * scrollMultiplier;
		}
	}
	
	public void ToggleWriteEnabled() {
		this.WriteEnabled = !this.WriteEnabled;
	}
	
	public void SetWriteEnabled(boolean value) {
		this.WriteEnabled = value;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX(), y = e.getY();
		
		if(x > memoryWidth - 10 && x < memoryWidth && y > (int)(((double)scrollYOffset / (double)(lastByteY)) * -(height - scrollBarHeight)) && y < (int)(((double)scrollYOffset / (double)(lastByteY)) * -(height - scrollBarHeight)) + scrollBarHeight) {
			DraggingScrollBar = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		DraggingScrollBar = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(DraggingScrollBar == true) {
			scrollYOffset = -(int)(((double)(e.getY()) / (double)height) * (double)lastByteY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
