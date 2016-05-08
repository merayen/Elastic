package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.NodeCreatedMessage;
import net.merayen.elastic.system.intercom.RemoveNodePortMessage;
import net.merayen.elastic.system.intercom.CreateNodePortMessage;
import net.merayen.elastic.util.Postmaster;

public abstract class BaseLogicNode {
	private String id; // Same ID as the one in NetList
	private Node node; // NetList-node that this LogicNode represents
	private LogicNodeList logicnode_list; // Only used for look-up needs

	/**
	 * Called when this node is created for the first time.
	 * You will need to initialize stuff like defining ports.
	 */
	protected abstract void onCreate();

	protected abstract void onMessageFromUI(Postmaster.Message message);
	protected abstract void onMessageFromBackend(Postmaster.Message message);

	protected abstract void onConnect(String port); // Port is not connected, but is now connected
	protected abstract void onDisconnect(String port); // When port was connected, but has no more connections

	protected void createPort(String name, boolean output, Format[] format) {
		if(format.length == 0)
			throw new RuntimeException("No format defined");

		if(output && format.length != 1)
			throw new RuntimeException("Output can only have 1 format");

		if(node.getPort(name) != null)
			throw new RuntimeException(String.format("Port %s already exist on node", name));

		Port port = node.createPort(name);
		port.properties.put("output", output);
		port.properties.put("format", format);

		// Notify both ways
		Postmaster.Message message = new CreateNodePortMessage(id, name, output, format);
		sendMessageToUI(message);
		sendMessageToBackend(message);
	}

	protected void createPort(String name, boolean output, Format format) {
		createPort(name, output, new Format[]{format});
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
