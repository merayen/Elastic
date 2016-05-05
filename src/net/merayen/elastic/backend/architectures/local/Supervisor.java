package net.merayen.elastic.backend.architectures.local;

/**
 * 
 */
public class Supervisor {
	LocalNetList netlist;

	public Supervisor(LocalNetList netlist) {
		this.netlist = netlist;
	}

	public void run() {
		initializeSystem();
	}

	/**
	 * Creates necessary session(s) in the LocalNetList.
	 */
	private void initializeSystem() {
		
	}
}
