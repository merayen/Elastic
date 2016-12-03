package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.pack.Dict;

/**
 * For now, the Supervisor does not support adding and removal of nodes/ports, connections etc. You will need to clear and restart.
 * TODO this class does 2 things now. Should perhaps only schedule and run the nodes, as it is a supervisor.
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

	private boolean dead;

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
		spawnMainSession();
	}

	/**
	 * Destroys everything and clears ourselves.
	 * Not much use of this instance after this call. Dispose it and create a new one.
	 */
	void clear() {
		dead = true;

		for(int session_id : processor_list.getSessions())
			removeSession(session_id);

		processor_list.clear();

		for(Node node : netlist.getNodes()) {
			local_properties.getLocalNode(node).onDestroy();
			local_properties.setLocalNode(node, null);
		}
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
			lp.localnode.onSpawnProcessor(lp);

		for(LocalProcessor lp : list)
			lp.init(sample_offset);
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

	/*
	 * Lazily removes a session.
	 * None of the LocalProcessors are actually deleted now, but rather collected later on.
	 */
	/*void removeSession(int session_id) {
		List<LocalProcessor> processors = processor_list.getSessionProcessors(session_id);
	}*/

	/**
	 * Called by a LocalProcessor to schedule it for processing.
	 */
	void schedule(LocalProcessor lp) {
		if(lp.localnode.supervisor != this)
			throw new RuntimeException("Should not happen");

		if(!scheduled.contains(lp))
			scheduled.add(lp);
	}

	/**
	 * Process a frame.
	 * Calls onProcess() on all processors until everyone is satisfied.
	 * Returns a new ProcessMessage() with the output data.
	 */
	public synchronized ProcessMessage process(ProcessMessage message) {
		if(dead)
			throw new RuntimeException("Should not be called when after clear()");

		// First let the LocalNode process, so they may create their default sessions and schedule processors to process
		for(Node node : netlist.getNodes()) {
			LocalNode ln = local_properties.getLocalNode(node);
			ln.onProcess((Dict)message.dict.data.get(node.getID()));
		}

		while(!scheduled.isEmpty()) { // TODO implement logic that detects hanging processors
			List<LocalProcessor> to_process = scheduled;
			scheduled = new ArrayList<>();
	
			// Then let all the processors process
			for(LocalProcessor lp : to_process)
				lp.doProcess();
		}

		// Make sure every processor has completely read and written to their buffers
		for(LocalProcessor lp : processor_list.getAllProcessors())
			if(!lp.frameFinished())
				System.out.printf("Node failed to process: %s. Frame has not been completely processed. Forgotten to increase Outlet.written / Inlet.read and called Outlet.push()?\n", lp.localnode.getClass().getName());

		ProcessMessage response = new ProcessMessage();

		// Notify all LocalNodes that we have processed.
		for(Node node : netlist.getNodes()) {
			LocalNode ln = local_properties.getLocalNode(node);

			ln.onFinishFrame();

			response.dict.data.put(node.getID(), ln.outgoing);
		}

		// Clean up dead sessions
		for(int session_id : new ArrayList<>(processor_list.getSessions())) {
			List<LocalProcessor> processors = processor_list.getProcessors(session_id);

			boolean active = false;

			for(LocalProcessor lp : processors)
				active |= lp.isActive();

			if(!active)
				removeSession(session_id);
		}

		return new ProcessMessage(); // TODO
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

	private void removeSession(int session_id) {
		System.out.println("Supervisor: Removing session " + session_id);
		for(LocalProcessor lp : processor_list.getProcessors(session_id))
			lp.onDestroy();

		processor_list.removeSession(session_id);
	}
}
