package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;
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
	private void wireUp(List<LocalProcessor> list) {
		for(LocalProcessor lp : list)
			lp.wireUp();
	}

	private void initProcessors(List<LocalProcessor> list) {
		for(LocalProcessor lp : list)
			lp.onInit();
	}

	/**
	 * Spawns a new session from a chain.
	 */
	synchronized int spawnSession(int chain_id) {
		session_id_counter++;

		if(processor_list.getChainSessions(chain_id).size() >= 128)
			throw new RuntimeException("Voice limit reached, can not spawn any more processors");

		List<LocalProcessor> to_wire_up = new ArrayList<>();
		for(Node node : netlist.getNodes()) {
			LocalNode local_node = local_properties.getLocalNode(node);
			for(int c : local_node.getChainIds()) {
				if(c == chain_id) {
					LocalProcessor local_processor = local_node.spawnProcessor(chain_id, session_id_counter);
					processor_list.add(local_processor);
					to_wire_up.add(local_processor);
					break;
				}
			}
		}

		wireUp(to_wire_up); // TODO only rewire the created ones
		initProcessors(to_wire_up);

		return session_id_counter;
	}

	/**
	 * Process a frame
	 */
	public void process() {
		// First let the LocalNode process, so they may create their default sessions
		for(Node node : netlist.getNodes())
			local_properties.getLocalNode(node).onProcess();

		// Then let all the processors process
		for(LocalProcessor lp : processor_list)
			lp.doProcess();
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
