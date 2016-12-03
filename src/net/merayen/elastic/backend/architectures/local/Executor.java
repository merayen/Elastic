package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.Config;
import net.merayen.elastic.backend.analyzer.Analyzer;
import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;

public class Executor extends AbstractExecutor {
	private NetList netlist = new NetList(); // Our local copy of the NetList that we build by the messages we receive. Contains properties only for us
	private NetList upcoming_netlist; // Is set to true when netlist has been changed and needing to analyze and prepare on next ProcessMessage()
	private Supervisor supervisor;

	Executor() {}

	@Override
	protected void onMessage(Postmaster.Message message) {
		if(message instanceof ProcessMessage) {
			applyNetList();
			ProcessMessage pm = supervisor.process((ProcessMessage)message);
			sendFromProcessing(pm);

		} else if(message instanceof NodeParameterMessage) {
			applyNetList();
			supervisor.handleMessage(message);

		} else if(message instanceof NetListMessage) {
			NetListMessages.apply(getUpcomingNetList(), message);

		}

		if(Config.DEBUG)
			System.out.printf("Executor got message %s\n", message);
	}

	/**
	 * Restarts ourself. Happens usually after when we have been messaged a message that
	 * requires us to start with a blank sheet.
	 */
	private void applyNetList() {
		if(upcoming_netlist != null) {
			netlist = upcoming_netlist;
			upcoming_netlist = null;

			Analyzer.analyze(netlist);
			if(supervisor != null)
				supervisor.clear();

			supervisor = new Supervisor(netlist, 44100, 512);
			supervisor.begin();
			System.out.println("Local architecture restarted");
		}
	}

	private NetList getUpcomingNetList() {
		if(upcoming_netlist == null) {
			upcoming_netlist = netlist.copy();
			new LocalNodeProperties().clear(upcoming_netlist);
		}

		return upcoming_netlist;
	}

	@Override
	public void stop() {
		supervisor.clear();
	}
}
