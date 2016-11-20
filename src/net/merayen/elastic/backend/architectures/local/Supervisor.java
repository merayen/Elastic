package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

/**
 * For now, the Supervisor does not support adding and removal of nodes/ports, connections etc. You will need to clear and restart.
 */
class Supervisor {
	private static final String CLASS_PATH = "net.merayen.elastic.backend.architectures.local.nodes.%s_%d.LNode";

	final NetList netlist;
	private final int sample_rate;
	private final int buffer_size;

	private final NodeProperties node_properties;
	private final LocalNodeProperties local_properties = new LocalNodeProperties();
	final ProcessorList processor_list = new ProcessorList();
	private int session_id_counter;

	private List<LocalProcessor> scheduled = new ArrayList<>(); // LocalProcessors scheduled for execution

	public Supervisor(NetList netlist, int sample_rate, int buffer_size) {
		this.netlist = netlist; // Our own, compiled NetList
		this.sample_rate = sample_rate;
		this.buffer_size = buffer_size;

		node_properties = new NodeProperties(netlist);
		load();
	}

	private void load() {
		for(Node node : netlist.getNodes()) {
			LocalNode localnode = local_properties.getLocalNode(node);

			if(localnode == null) { // LocalNode could already be loaded and assigned when doing tests
				String path = String.format(CLASS_PATH, node_properties.getName(node), node_properties.getVersion(node));
				try {
					@SuppressWarnings("unchecked")
					Class<? extends LocalNode> cls = (Class<? extends LocalNode>)Class.forName(path);
					localnode = cls.newInstance();
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					System.out.printf("Could not load Node %s\n", path);
					throw new RuntimeException(e);
				}

				local_properties.setLocalNode(node, localnode);
			}

			localnode.compiler_setInfo(this, node, sample_rate, buffer_size);
			localnode.init();
		}
	}

	void begin() {
		// Launch all main-session nodes
		spawnMainSession();
	}

	void end() {
		// TODO end every node and processor
	}

	public void handleMessage(Postmaster.Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			LocalNode localnode = local_properties.getLocalNode(netlist.getNode(m.node_id));
			localnode.onParameter(m.key, m.value);
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

	private void initProcessors(List<LocalProcessor> list, int sample_offset) {
		for(LocalProcessor lp : list)
			lp.init(sample_offset);
	}

	private void prepareProcessors(ProcessMessage message) {
		for(LocalProcessor lp : processor_list)
			lp.prepare(message.data.get(lp.localnode.getID()));
	}

	int spawnSession(int chain_id, int sample_offset) {
		spawnSession(chain_id, ++session_id_counter, sample_offset);
		return session_id_counter;
	}

	/**
	 * Spawns a new session from a chain.
	 * sample_offset is the offset into the process frame which the session gets spawned.
	 */
	synchronized private void spawnSession(int chain_id, int session_id, int sample_offset) {
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

		wireUp(to_wire_up);
		initProcessors(to_wire_up, sample_offset);
	}

	/**
	 * Spawns the main session, which always has the session_id 0.
	 */
	void spawnMainSession() {
		spawnSession(0, 0, 0);
	}

	/**
	 * Called by a LocalProcessor to schedule it for processing.
	 */
	void schedule(LocalProcessor localprocessor) {
		if(!scheduled.contains(localprocessor))
			scheduled.add(localprocessor);
	}

	/**
	 * Process a frame.
	 * Calls onProcess() on all processors until everyone is satisfied
	 */
	public synchronized void process(ProcessMessage message) {
		prepareProcessors(message);

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

		for(LocalProcessor lp : processor_list.getAllProcessors())
			if(!lp.frameFinished())
				System.out.printf("Node failed to process: %s. Frame has not been completely processed. Forgotten to increase Outlet.written / Inlet.read and called Outlet.push()?\n", lp.localnode.getClass().getName());
	}

	public LocalNode getLocalNode(String id) {
		return local_properties.getLocalNode(netlist.getNode(id));
	}

	public LocalProcessor getProcessor(LocalNode localnode, int session_id) {
		return processor_list.getProcessor(localnode, session_id);
	}

	public List<LocalProcessor> getProcessors(LocalNode localnode) {
		List<LocalProcessor> result = new ArrayList<>();
		for(LocalProcessor lp : processor_list.getAllProcessors())
			if(lp.localnode == localnode)
				result.add(lp);

		return result;
	}
}
