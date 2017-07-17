package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.Config;
import net.merayen.elastic.backend.analyzer.Analyzer;
import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;

public class Executor extends AbstractExecutor {
	public Executor(InitBackendMessage message) {
		super(message);
	}

	private NetList netlist = new NetList(); // Our local copy of the NetList that we build by the messages we receive. Contains properties only for us
	private NetList upcoming_netlist; // Is set to true when netlist has been changed and needing to analyze and prepare on next ProcessMessage()
	private Supervisor supervisor;

	@Override
	protected void onMessage(Postmaster.Message message) {
		if(message instanceof ProcessMessage) {
			applyNetList();

			if(Config.processor.debug.verbose)
				System.out.printf("Executor: Processing with a NetList of %d nodes, %d connections\n", netlist.getNodes().size(), netlist.getLines().size());

			if(supervisor == null) { // Got nothing to respond with, we are completely empty
				sendFromProcessing(new ProcessMessage());
				return;
			}

			ProcessMessage pm = supervisor.process((ProcessMessage)message);
			sendFromProcessing(pm);

		} else if(message instanceof NodeParameterMessage || message instanceof NodeDataMessage) {
			applyNetList();
			supervisor.handleMessage(message);
			NetListMessages.apply(getNetList(), message);

		} else if(message instanceof NetListMessage) {
			NetListMessages.apply(branchNetList(), message);

		}

		if(Config.processor.debug.messages)
			System.out.printf("Executor: Got message %s\n", message);
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

			supervisor = new Supervisor(netlist, sample_rate, sample_buffer_size);
			supervisor.begin();

			if(Config.processor.debug.verbose)
				System.out.println("Executor: Local architecture restarted");
		}
	}

	private NetList getNetList() {
		if(upcoming_netlist == null)
			return netlist;

		return upcoming_netlist;
	}

	private NetList branchNetList() {
		if(upcoming_netlist == null) {
			upcoming_netlist = netlist.copy();
			new LocalNodeProperties().clear(upcoming_netlist);

			if(Config.processor.debug.verbose)
				System.out.println("Executor: Upcoming NetList created");
		}

		return upcoming_netlist;
	}

	@Override
	public void stop() {
		supervisor.clear();
	}
}
