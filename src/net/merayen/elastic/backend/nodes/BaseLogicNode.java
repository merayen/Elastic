package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

public abstract class BaseLogicNode {
	/**
	 * Instantiate, edit and pass into createPort() to create ports.
	 */
	public static class PortDefinition {
		public String name; // Name of the port
		public boolean output; // If port is output or not
		public Format[] format; // Array of formats you support
		public String chain_ident; // TODO rename to chain_ident
	}

	private String id; // Same ID as the one in NetList
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

	protected void createPort(PortDefinition def) {
		if(def.name == null || def.name.length() == 0)
			throw new RuntimeException("Invalid port name");

		if(netlist.getPort(node, def.name) != null)
			throw new RuntimeException(String.format("Port %s already exist on node", def.name));

		if(def.format.length == 0)
			throw new RuntimeException("No format defined");

		if(def.output && def.format.length != 1)
			throw new RuntimeException("Output port can only have 1 format");

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
	void setInfo(String id, LogicNodeList logicnode_list, NetList netlist, Node node) {
		this.id = id;
		this.logicnode_list = logicnode_list;
		this.netlist = netlist;
		this.node = node;
	}

	void notifyConnect(String port) {
		// TODO logic that keeps track if this is the first connecting line
		onConnect(port);
	}

	void notifyDisconnect(String port) {
		// TODO logic that keeps track if this is the last line disappearing
		onDisconnect(port);
	}

	public String getID() {
		return id;
	}

	protected void sendMessageToUI(Postmaster.Message message) {
		logicnode_list.sendMessageToUI(message);
	}

	protected void sendMessageToBackend(Postmaster.Message message) {
		logicnode_list.sendMessageToBackend(message);
	}

	// TODO implement functions for introspection into NetList
}