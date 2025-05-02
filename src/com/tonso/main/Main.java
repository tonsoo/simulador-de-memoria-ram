package com.tonso.main;

import com.tonso.computer.ComputerTerminal;
import com.tonso.computer.Memory;
import com.tonso.computer.Processor;

public class Main implements Runnable {
	
	private Memory memory;
	private Processor processor;
	private ComputerTerminal terminal;
	
	private boolean running = false;
	
	private Thread t;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
//		ProgramCompiler c = new ProgramCompiler();
		memory = new Memory(640, 480, 8);
		processor = new Processor(memory);
		terminal = new ComputerTerminal(480, 340, processor);
		
		start();
	}
	
	synchronized void start() {
		running = true;
		
		t = new Thread(this);
		t.start();
	}
	
	synchronized void stop() {
		running = false;
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void Update() {
		if(memory != null) {			
			memory.Update();
		}
		if(processor != null) {			
			processor.Update();
		}
		if(terminal != null) {			
			terminal.Update();
		}
	}

	@Override
	public void run() {
		while(running) {			
			try {
				Thread.sleep(1000/60);
				Update();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
