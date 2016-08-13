package net.merayen.elastic.backend.architectures.local;

import java.util.Set;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

class Supervisor {
	final NetList netlist;
	private final NodeProperties properties;
	private final LocalNodeProperties local_properties = new LocalNodeProperties();
	final ProcessorList processor_list = new ProcessorList();
	private int session_id_counter;

	public Supervisor(NetList netlist) {
		this.netlist = netlist; // Our own, compiled NetList
		properties = new NodeProperties(netlist);
	}

	void begin() {
		// Launch all main-session nodes
		spawnSession(0);
	}

	public void handleMessage(Postmaster.Message message) {
		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			// TODO create LocalNode and update any running voices
		}
	}

	/**
	 * Makes sure all outlets and inlets are connected.
	 * Call after LocalProcessors has been created and needs to be reconnected.
	 */
	void wireUp() {
		for(LocalProcessor lp : processor_list)
			lp.wireUp();
	}

	/**
	 * Spawns a new session from a chain.
	 */
	int spawnSession(int chain_id) {
		session_id_counter++;

		for(Node node : netlist.getNodes()) {
			LocalNode local_node = local_properties.getLocalNode(node);
			for(int c : local_node.getChainIds()) {
				if(c == chain_id) {
					LocalProcessor local_processor = local_node.spawnProcessor(chain_id, session_id_counter);
					processor_list.add(local_processor);
					break;
				}
			}
		}

		wireUp();

		return session_id_counter;
	}

	/**
	 * Process a frame
	 */
	public void process() {
		for(LocalProcessor lp : processor_list)
			lp.onProcess();
	}

	public LocalNode getLocalNode(String id) {
		return local_properties.getLocalNode(netlist.getNode(id));
	}

	public LocalProcessor getProcessor(LocalNode localnode, int session_id) {
		return processor_list.getProcessor(localnode, session_id);
	}

	public Set<Integer> getSessions(LocalNode localnode) {
		return processor_list.getSessions();
	}
}
