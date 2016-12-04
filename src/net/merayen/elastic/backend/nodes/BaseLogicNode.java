package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.pack.PackDict;

public abstract class BaseLogicNode {
	/**
	 * Instantiate, edit and pass into createPort() to create ports.
	 */
	public static class PortDefinition {
		public String name; // Name of the port
		public boolean output; // If port is output or not
		public Format format; // Format the port uses. Only for output-ports
		public String chain_ident; // TODO rename to chain_ident
	}

	private Environment env;

	private String id; // Same ID as the one in NetList
	private Supervisor supervisor;
	private Node node; // NetList-node that this LogicNode represents
	private NetList netlist;
	private LogicNodeList logicnode_list; // Only used for look-up needs

	/**
	 * Called when this node is created for the first time.
	 * You will need to initialize stuff like defining ports.
	 */
	protected abstract void onCreate();

	/**
	 * Called when a parameter change has occured.
	 * This could be the UI changing a parameter, or a restore loading parameters.
	 * Modify value if needed and call set(...) to acknowledge, which will send it to UI and the backend.
	 */
	protected abstract void onParameterChange(NodeParameterMessage message);

	protected abstract void onConnect(String port); // Port is not connected, but is now connected
	protected abstract void onDisconnect(String port); // Port was connected, but has no more connections

	/**
	 * Called when a ProcessMessage()-request has been received. All LogicNodes must prepare data to be sent to processor.
	 * Change the data-argument directly. 
	 */
	protected abstract void onPrepareFrame(PackDict data);

	/**
	 * Called when the processor has processed.
	 * The result data is put into the data argument.
	 */
	protected abstract void onFinishFrame(PackDict data);

	protected void createPort(PortDefinition def) {
		if(def.name == null || def.name.length() == 0)
			throw new RuntimeException("Invalid port name");

		if(netlist.getPort(node, def.name) != null)
			throw new RuntimeException(String.format("Port %s already exist on node", def.name));

		if(def.output && def.format == null)
			throw new RuntimeException("Output-ports must have a format");

		if(!def.output && def.format != null)
			throw new RuntimeException("Input-ports can not have a format set, as it will depend on the output-port it is connected to");

		// Notify both ways
		Postmaster.Message message = new CreateNodePortMessage(id, def.name, def.output, def.format, def.chain_ident); // TODO rename to chain_ident
		sendMessageToUI(message);
		sendMessageToBackend(message);
	}

	protected void removePort(String name) {
		if(netlist.getPort(node, name) == null)
			throw new RuntimeException(String.format("Port %s does not exist on Node", name));

		netlist.removePort(node, name);

		// Notify both ways
		Postmaster.Message message = new RemoveNodePortMessage(id, name);
		sendMessageToUI(message);
		sendMessageToBackend(message);
	}

	protected void set(String key, Object value) {
		NodeParameterMessage message = new NodeParameterMessage(id, key, value);
		sendMessageToBackend(message);
		sendMessageToUI(message);
	}

	protected void set(NodeParameterMessage message) {
		sendMessageToBackend(message);
		sendMessageToUI(message);
	}

	void create(String name, Integer version) {
		CreateNodeMessage m = new CreateNodeMessage(id, name, version);
		sendMessageToUI(m); // Acknowledges creation of Node to the UI
		sendMessageToBackend(m); // Notify the backend too
		onCreate();
	}

	/**
	 * Only used by the 
	 */
	void setInfo(String id, Supervisor supervisor, Node node) {
		this.id = id;
		this.supervisor = supervisor;
		this.logicnode_list = supervisor.logicnode_list;
		this.netlist = supervisor.netlist;
		this.node = node;
		this.env = env;
	}

	void notifyConnect(String port) {
		// TODO logic that keeps track if this is the first connecting line. Why?
		onConnect(port);
	}

	void notifyDisconnect(String port) {
		// TODO logic that keeps track if this is the last line disappearing. Why?
		onDisconnect(port);
	}

	public String getID() {
		return id;
	}

	protected Environment getEnv() {
		return env;
	}

	/**
	 * Send message to UI (frontend).
	 * 
	 */
	protected void sendMessageToUI(Postmaster.Message message) {
		logicnode_list.sendMessage(message);
	}

	/**
	 * Send message to processor.
	 */
	protected void sendMessageToProcessor(Postmaster.Message message) {
		logicnode_list.sendMessageToBackend(message);
	}

	// TODO implement functions for introspection into NetList
}