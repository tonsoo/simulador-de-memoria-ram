package com.tonso.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public abstract class Screen extends Canvas implements MouseWheelListener, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int width=640, height=480;
	
	private int closeOperation;
	private String title="New window";
	
	private JFrame screenFrame;
	
	public Screen(int width, int height, String title, int closeOperation) {
		this.width = width;
		this.height = height;
		this.title = title;
		this.closeOperation = closeOperation;
		
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setPreferredSize(new Dimension(this.width, this.height));
		
		InitFrame();
	}
	
	protected void InitFrame() {
		if(this.screenFrame != null) {
			this.screenFrame.dispose();
		}
		this.screenFrame = new JFrame(this.title);
		screenFrame.add(this);
		screenFrame.setResizable(false);
		screenFrame.setDefaultCloseOperation(this.closeOperation);
		screenFrame.pack();
		screenFrame.setLocationRelativeTo(null);
		screenFrame.setVisible(true);
		screenFrame.requestFocus();
	}
	
	public void Update() {
		GetGraphics();
		this.FixedUpdate();
	}
	
	private void GetGraphics() {
		try {
			BufferStrategy bs = this.getBufferStrategy();
			
			if(bs == null) {
				this.createBufferStrategy(3);
				return;
			}
			
			Graphics g = bs.getDrawGraphics();
			
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0,  0, width, height);
			
			this.Render(g);
			
			bs.show();	
		} catch(Exception e) {
//			e.printStackTrace();
		}
	}
	
	public abstract void FixedUpdate();
	
	public abstract void Render(Graphics g);
}
