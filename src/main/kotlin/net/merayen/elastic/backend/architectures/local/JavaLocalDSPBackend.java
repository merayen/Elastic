package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.Config;
import net.merayen.elastic.Temporary;
import net.merayen.elastic.backend.analyzer.Analyzer;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.system.DSPModule;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.NetListMessages;

public class JavaLocalDSPBackend extends DSPModule {
	private NetList netlist = new NetList(); // Our local copy of the NetList that we build by the messages we receive. Contains properties only for us
	private NetList upcoming_netlist; // Is set to true when netlist has been changed and needing to analyze and prepare on next ProcessMessage()
	private Supervisor supervisor;

	public JavaLocalDSPBackend() {
		super();
		setName("JavaLocalDSPBackend");
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onUpdate() {
		for (ElasticMessage message : getIngoing().receiveAll())
			handleMessage(message);
	}

	@Override
	public void onEnd() {
		if (supervisor != null)
			supervisor.clear();
	}

	private void handleMessage(ElasticMessage message) {
		if (message instanceof ProcessRequestMessage) {
			applyNetList();

			if (Config.processor.debug.verbose)
				System.out.printf("Executor: Processing with a NetList of %d nodes, %d connections\n", netlist.getNodes().size(), netlist.getLines().size());

			if (supervisor == null) { // Got nothing to respond with, we are completely empty
				getOutgoing().send(new ProcessResponseMessage());
				notifyElasticSystem();
				return;
			}

			ProcessResponseMessage pm = supervisor.process((ProcessRequestMessage) message);
			getOutgoing().send(pm);
			notifyElasticSystem();

		} else if (message instanceof NodePropertyMessage || message instanceof NodeDataMessage) {
			applyNetList();
			supervisor.handleMessage(message);
			NetListMessages.INSTANCE.apply(getNetList(), message);

		} else if (message instanceof NetListMessage) {
			NetListMessages.INSTANCE.apply(branchNetList(), message);

		}

		if (Config.processor.debug.messages)
			System.out.printf("Executor: Got message %s\n", message);
	}

	/**
	 * Restarts our self. Happens usually after when we have been messaged a message that
	 * requires us to start with a blank sheet.
	 */
	private void applyNetList() {
		if (upcoming_netlist != null) {
			netlist = upcoming_netlist;
			upcoming_netlist = null;

			Analyzer.analyze(netlist);
			if (supervisor != null)
				supervisor.clear();

			supervisor = new Supervisor(netlist, Temporary.sampleRate, Temporary.bufferSize);
			supervisor.begin();

			if (Config.processor.debug.verbose)
				System.out.println("Executor: Local architecture restarted");
		}
	}

	private NetList getNetList() {
		if (upcoming_netlist == null)
			return netlist;

		return upcoming_netlist;
	}

	private NetList branchNetList() {
		if (upcoming_netlist == null) {
			upcoming_netlist = netlist.copy();

			if (Config.processor.debug.verbose)
				System.out.println("Executor: Upcoming NetList created");
		}

		return upcoming_netlist;
	}
}
