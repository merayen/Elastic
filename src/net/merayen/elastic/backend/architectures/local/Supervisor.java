package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

/**
 * 
 */
public class Supervisor {
	NetList netlist;

	public Supervisor(NetList netlist) {
		this.netlist = netlist;
	}

	public void run() {
		initializeSystem();
	}

	public void handleMessage(Postmaster.Message message) {
		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			// TODO create LocalNode and update any running voices
		}
	}

	/**
	 * Creates necessary session(s) in the LocalNetList.
	 */
	private void initializeSystem() {
		
	}
}
