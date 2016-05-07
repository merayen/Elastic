package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.util.Postmaster;

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
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleMessage(Postmaster.Message message) {
		System.out.printf("Local processing architecture got this message: %s\n", message);
	}
}
