package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.NodeCreatedMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.RemoveNodePortMessage;
import net.merayen.elastic.system.intercom.CreateNodePortMessage;
import net.merayen.elastic.util.Postmaster;

public abstract class BaseLogicNode {
	/**
	 * Instantiate, edit and pass into createPort() to create ports.
	 */
	public static class PortDefinition {
		public String name; // Name of the port
		public boolean output; // If port is output or not
		public Format[] format; // Array of formats you support
		public int poly_no = -1; // voice no, allows multiple voice group output from your node. Negative value means "no poly"
	}

	private String id; // Same ID as the one in NetList
	private Node node; // NetList-node that this LogicNode represents
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
	protected abstract void onParameterChange(String key, Object value);
	protected abstract void onMessageFromBackend(Postmaster.Message message);

	protected abstract void onConnect(String port); // Port is not connected, but is now connected
	protected abstract void onDisconnect(String port); // Port was connected, but has no more connections

	protected void createPort(PortDefinition def) {
		if(def.name == null || def.name.length() == 0)
			throw new RuntimeException("Invalid port name");

		if(node.getPort(def.name) != null)
			throw new RuntimeException(String.format("Port %s already exist on node", def.name));

		if(def.format.length == 0)
			throw new RuntimeException("No format defined");

		if(def.output && def.format.length != 1)
			throw new RuntimeException("Output port can only have 1 format");

		Port port = node.createPort(def.name);
		port.properties.put("output", def.output);
		port.properties.put("format", Format.toStrings(def.format));

		if(def.poly_no > -1)
			port.properties.put("poly_no", def.poly_no);

		// Notify both ways
		Postmaster.Message message = new CreateNodePortMessage(id, def.name, def.output, def.format, def.poly_no);
		sendMessageToUI(message);
		sendMessageToBackend(message);
	}

	protected void removePort(String name) {
		if(node.getPort(name) == null)
			throw new RuntimeException(String.format("Port %s does not exist on Node", name));

		node.removePort(name);

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

	void create(String name, Integer version) {
		sendMessageToUI(new NodeCreatedMessage(id, name, version)); // Acknowledges creation of Node to the UI
		onCreate();
	}

	/**
	 * Only used by the 
	 */
	void setInfo(String id, LogicNodeList logicnode_list, Node node) {
		this.id = id;
		this.logicnode_list = logicnode_list;
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