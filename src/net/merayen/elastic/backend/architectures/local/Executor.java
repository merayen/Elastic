package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.AbstractExecutor;

public class Executor extends AbstractExecutor {

	private LocalNetList netlist;

	public Executor(LocalNetList netlist) {
		this.netlist = netlist;
	}

	/**
	 * Puts the processor system in use and processes ...
	 */
	public void process() {
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
