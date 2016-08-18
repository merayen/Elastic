package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

class Supervisor {
	final NetList netlist;
	//private final NodeProperties properties;
	private final LocalNodeProperties local_properties = new LocalNodeProperties();
	final ProcessorList processor_list = new ProcessorList();
	private int session_id_counter;

	private List<LocalProcessor> scheduled = new ArrayList<>(); // LocalProcessors scheduled for execution

	public Supervisor(NetList netlist) {
		this.netlist = netlist; // Our own, compiled NetList
		//properties = new NodeProperties(netlist);
	}

	void begin() {
		// Launch all main-session nodes
		spawnMainSession();
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

	private void prepareProcessors() {
		for(LocalProcessor lp : processor_list)
			lp.prepare();
	}

	int spawnSession(int chain_id) {
		session_id_counter++;
		return spawnSession(chain_id, session_id_counter);
	}

	/**
	 * Spawns a new session from a chain.
	 */
	synchronized private int spawnSession(int chain_id, int session_id) {
		if(processor_list.getChainSessions(chain_id).size() >= 128)
			throw new RuntimeException("Voice limit reached, can not spawn any more processors");

		List<LocalProcessor> to_wire_up = new ArrayList<>();
		for(Node node : netlist.getNodes()) {
			LocalNode local_node = local_properties.getLocalNode(node);
			for(int c : local_node.getChainIds()) {
				if(c == chain_id) {
					LocalProcessor local_processor = local_node.spawnProcessor(chain_id, session_id);
					processor_list.add(local_processor);
					to_wire_up.add(local_processor);
					break;
				}
			}
		}

		wireUp(to_wire_up); // TODO only rewire the created ones
		initProcessors(to_wire_up);

		return session_id;
	}

	/**
	 * Spawns the main session, which always has the session_id 0.
	 */
	void spawnMainSession() {
		spawnSession(0, 0);
	}

	/**
	 * Called by a LocalProcessor to schedule it for processing.
	 */
	void schedule(LocalProcessor localprocessor) {
		scheduled.add(localprocessor);
	}

	/**
	 * Process a frame.
	 * Calls onProcess() on all processors until everyone is satisfied
	 */
	public synchronized void process() {
		prepareProcessors();

		// First let the LocalNode process, so they may create their default sessions and schedule processors to process
		for(Node node : netlist.getNodes())
			local_properties.getLocalNode(node).onProcess();

		while(!scheduled.isEmpty()) { // TODO implement logic that detects hanging processors
			List<LocalProcessor> to_process = scheduled;
			scheduled = new ArrayList<>();
	
			// Then let all the processors process
			for(LocalProcessor lp : to_process)
				lp.doProcess();
		}
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
