package net.merayen.elastic.backend.nodes;

import java.util.Map;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.NetListMessages;

public abstract class BaseLogicNode {
	/**
	 * Instantiate, edit and pass into createPort() to create ports.
	 */
	public static class PortDefinition {
		public String name; // Name of the port
		public boolean output; // If port is output or not
		public Format format; // Format the port uses. Only for output-ports

		public PortDefinition() {}

		/** Define a input port **/
		public PortDefinition(String name) {
			this.name = name;
		}

		/** Define an output port with format **/
		public PortDefinition(String name, Format format) {
			this.name = name;
			this.format = format;
			this.output = true;
		}
	}

	private LogicEnvironment env;

	private String id; // Same ID as the one in NetList
	private Supervisor supervisor;
	Node node; // NetList-node that this LogicNode represents
	private NetList netlist;
	private LogicNodeList logicnode_list; // Only used for look-up needs
	boolean inited;
	private NodeProperties np;

	/**
	 * Called when this node is created for the first time.
	 * You will need to initialize stuff like defining ports.
	 */
	protected abstract void onCreate();

	/**
	 * Called every time the LogicNode class is created (either by creating a new node or loading from existing node).
	 * Called after onCreate().
	 */
	protected abstract void onInit();

	/**
	 * Called when a parameter change has occurred.
	 * This could be the UI changing a parameter, or a restore loading parameters.
	 * Modify value if needed and call set(...) to acknowledge, which will send it to UI and the backend.
	 */
	protected abstract void onParameterChange(String key, Object value);

	/**
	 * Data received from UI.
	 */
	protected abstract void onData(Object data);

	protected abstract void onConnect(String port); // Port is not connected, but is now connected
	protected abstract void onDisconnect(String port); // Port was connected, but has no more connections

	/**
	 * Called when node is being deleted.
	 */
	protected abstract void onRemove();

	/**
	 * Called when a ProcessMessage()-request has been received. All LogicNodes must prepare data to be sent to processor.
	 * Change the data-argument directly. 
	 */
	protected abstract void onPrepareFrame(Map<String, Object> data);

	/**
	 * Called when the processor has processed.
	 * The result data is put into the data argument.
	 */
	protected abstract void onFinishFrame(OutputFrameData data);

	/**
	 * Call this to create a port.
	 * All LogicNodes needs to this on creation. This will add the ports in the UI and the processor.
	 */
	protected void createPort(PortDefinition def) {
		if(def.name == null || def.name.length() == 0)
			throw new RuntimeException("Invalid port name");

		if(netlist.getPort(node, def.name) != null)
			throw new RuntimeException(String.format("Port %s already exist on node", def.name));

		if(def.output && def.format == null)
			throw new RuntimeException("Output-ports must have a format");

		if(!def.output && def.format != null)
			throw new RuntimeException("Input-ports can not have a format set, as it will depend on the output-port it is connected to");

		// Everything OK
		Object message = new CreateNodePortMessage(id, def.name, def.output, def.format); // TODO rename to chain_ident

		NetListMessages.INSTANCE.apply(netlist, message); // Apply the port to the NetList

		supervisor.sendMessageToUI(message);
		supervisor.sendMessageToProcessor(message);

	}

	protected void removePort(String name) {
		supervisor.removePort(this, name);
	}

	public void set(String key, Object value) {
		NodeParameterMessage message = new NodeParameterMessage(id, key, value);
		supervisor.sendMessageToProcessor(message);
		supervisor.sendMessageToUI(message);

		np.parameters.set(node, key, value);
	}

	public Object getParameter(String key) {
		return np.parameters.get(node, key);
	}

	protected void sendDataToUI(NodeDataMessage data) {
		sendMessageToUI(data);
	}

	protected boolean isConnected(String port) {
		return !netlist.getConnections(node, port).isEmpty();
	}

	protected String[] getPorts() {
		return netlist.getPorts(node);
	}

	protected boolean isOutput(String port) {
		return np.isOutput(netlist.getPort(node, port));
	}

	void create(String name, Integer version, String parent) {
		CreateNodeMessage m = new CreateNodeMessage(id, name, version, parent);
		supervisor.sendMessageToUI(m); // Acknowledges creation of Node to the UI
		supervisor.sendMessageToProcessor(m); // Notify the backend too

		if(!supervisor.restoring)
			onCreate();
	}

	void processData(Object data) {
		onData(data);

		//if(data.containsKey("node_stats"))
		//	sendMessageToUI((NodeStatusMessage)data.get("node_stats"));
	}

	/**
	 * Only used by the 
	 */
	void setInfo(String id, Supervisor supervisor, Node node) {
		this.id = id;
		this.supervisor = supervisor;
		this.node = node;

		env = supervisor.env;
		logicnode_list = supervisor.logicnode_list;
		netlist = ((Environment)supervisor.env).project.getNetList();
		np = new NodeProperties(netlist);
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

	protected LogicEnvironment getEnv() {
		return env;
	}

	/**
	 * Send message to UI (frontend).
	 * 
	 */
	protected void sendMessageToUI(Object message) {
		supervisor.sendMessageToUI(message);
	}

	/**
	 * Send message to processor.
	 */
	protected void sendMessageToProcessor(Object message) {
		supervisor.sendMessageToProcessor(message);
	}

	// TODO implement functions for introspection into NetList
}