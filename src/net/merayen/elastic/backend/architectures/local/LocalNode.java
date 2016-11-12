package net.merayen.elastic.backend.architectures.local;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

/**
 * A LocalNode is a node that implements the logic for local JVM processing.
 * All nodes must implement this.
 * Its processors are the ones actually performing the work.
 * LocalNodes receives parameter changes and behaves after that.
 */
public abstract class LocalNode {
	Supervisor supervisor;
	NetList netlist;
	Node node; // The NetList-node we represent
	int buffer_size;
	private final Class<? extends LocalProcessor> processor_cls;
	private final NodeProperties properties = new NodeProperties(netlist);
	private final Set<Integer> chain_ids = new HashSet<>(); // chain_ids this node is member of. Calculated from the ports

	protected abstract void onInit();
	protected abstract void onProcess();
	protected abstract void onParameter(String key, Object value);
	protected abstract void onDestroy();

	public LocalNode(Class<? extends LocalProcessor> cls) {
		processor_cls = cls;
	}

	public String getID() {
		return node.getID();
	}

	void compiler_setInfo(Supervisor supervisor, Node node, int buffer_size) {
		this.supervisor = supervisor;
		this.netlist = supervisor.netlist;
		this.node = node;
		this.buffer_size = buffer_size;

		// Figure out all the chain ids this node belongs to
		for(String port_name : netlist.getPorts(node))
			for(int port_chain_id : properties.analyzer.getPortChainIds(netlist.getPort(node, port_name)))
				chain_ids.add(port_chain_id);
	}

	/**
	 * Start a session from an output-port.
	 * @return	The session_id of the new voice
	 */
	protected synchronized int spawnVoice(String port_name) {
		Port port = netlist.getPort(node, port_name);

		if(!properties.isOutput(port))
			throw new RuntimeException("Can not spawn voice on input ports");

		String ident = properties.getPortChainIdent(port);

		if(ident == null)
			throw new RuntimeException("Port does not have a chain ident, and thus, a voice can not be spawned");

		int chain_id = properties.analyzer.getPortChainCreateId(port);

		return supervisor.spawnSession(chain_id);
	}

	/**
	 * Gets all the chain_ids this LocalNode is able to create.
	 */
	Set<Integer> getChainIds() {
		return chain_ids;
	}

	/**
	 * Returns LocalProcessor for this LocalNode
	 */
	LocalProcessor spawnProcessor(int chain_id, int session_id) {
		// Create the processor
		LocalProcessor lp;
		try {
			lp = (LocalProcessor)processor_cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		// Set information
		lp.LocalProcessor_setInfo(this, chain_id, session_id);

		return lp;
	}

	void init() {
		onInit();
	}

	protected LocalProcessor getProcessor(int session_id) {
		return supervisor.getProcessor(this, session_id);
	}

	protected List<LocalProcessor> getProcessors() {
		return supervisor.getProcessors(this);
	}
}
