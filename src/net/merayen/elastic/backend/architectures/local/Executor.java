package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.analyzer.Analyzer;
import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;

public class Executor extends AbstractExecutor {
	private NetList netlist = new NetList(); // Our local copy of the NetList that we build by the messages we receive
	private NetList upcoming_netlist; // Is set to true when netlist has been changed and needing to analyze and prepare on next ProcessMessage()
	private Supervisor supervisor;

	Executor() {}

	@Override
	protected void onMessage(Postmaster.Message message) {
		if(message instanceof ProcessMessage) {
			applyNetList();
			supervisor.process((ProcessMessage)message);

		} else if(message instanceof CreateNodeMessage) {
			NetListMessages.apply(getUpcomingNetList(), message);

		} else if(message instanceof CreateNodePortMessage) {
			NetListMessages.apply(getUpcomingNetList(), message);

		} else if(message instanceof RemoveNodePortMessage) {
			NetListMessages.apply(getUpcomingNetList(), message);

		} else if(message instanceof NodeConnectMessage) {
			NetListMessages.apply(getUpcomingNetList(), message);

		} else if(message instanceof NodeDisconnectMessage) {
			NetListMessages.apply(getUpcomingNetList(), message);

		} else if(message instanceof NodeParameterMessage) {
			applyNetList();
			supervisor.handleMessage(message);
		}

		System.out.printf("Executor got message %s\n", message);
	}

	/**
	 * Restarts ourself. Happens usually when we are messaged a message that
	 * requires blank sheet.
	 */
	private void applyNetList() {
		if(upcoming_netlist != null) {
			netlist = upcoming_netlist;
			upcoming_netlist = null;

			Analyzer.analyze(netlist);
			supervisor = new Supervisor(netlist);
			System.out.println("Local architecture restarted");
			
		}
	}

	private NetList getUpcomingNetList() {
		if(upcoming_netlist == null)
			upcoming_netlist = netlist.copy();

		return upcoming_netlist;
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void update() {
		/*try {
			wait();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}*/
	}
}
