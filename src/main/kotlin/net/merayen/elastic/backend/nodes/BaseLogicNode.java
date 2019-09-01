package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.ClassInstanceMerger;
import net.merayen.elastic.util.JSONObjectMapper;
import net.merayen.elastic.util.NetListMessages;

import java.util.Map;

public abstract class BaseLogicNode {
	private LogicEnvironment env;

	private String id; // Same ID as the one in NetList
	private Supervisor supervisor;
	Node node; // NetList-node that this LogicNode represents
	private NetList netlist;
	private LogicNodeList logicnode_list; // Only used for look-up needs
	boolean inited;
	private NodeProperties np;

	//private InheritanceTree parameters;

	private JSONObjectMapper mapper;
	public BaseNodeData properties;

	/**
	 * Called when this node is created for the first time.
	 * You will need to initialize stuff like defining ports.
	 * TODO remove. Should rather do this in onInit, and check if e.g any ports are missing
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
	 * @param instance
	 */
	protected abstract void onParameterChange(BaseNodeData instance);

	/**
	 * Data received from UI. Data is not meant for storing, and is usually for streaming etc
	 */
	protected abstract void onData(NodeDataMessage data);

	protected abstract void onConnect(String port); // Port is not connected, but is now connected

	protected abstract void onDisconnect(String port); // Port was connected, but has no more connections

	/**
	 * Called when node is being deleted.
	 */
	protected abstract void onRemove();

	/**
	 * Called when a ProcessMessage()-request has been received. All LogicNodes must prepare data to be sent to processor.
	 * Change the data-argument directly.
	 *
	 * @return
	 */
	protected InputFrameData onPrepareFrame() {
		return new InputFrameData(id);
	}

	/**
	 * Called when the processor has processed.
	 * The result data is put into the data argument.
	 */
	protected abstract void onFinishFrame(OutputFrameData data);

	/**
	 * Call this to create a port.
	 * All LogicNodes needs to this on creation. This will add the ports in the UI and the processor.
	 */
	private void createPort(String name, Format format) {
		boolean output = format != null;

		if (name == null || name.length() == 0)
			throw new RuntimeException("Invalid port name");

		if (netlist.getPort(node, name) != null)
			throw new RuntimeException(String.format("Port %s already exist on node", name));

		// Everything OK
		ElasticMessage message = new CreateNodePortMessage(id, name, output, format); // TODO rename to chain_ident

		NetListMessages.INSTANCE.apply(netlist, message); // Apply the port to the NetList

		supervisor.sendMessageToUI(message);
		supervisor.sendMessageToProcessor(message);
	}

	protected void createInputPort(String name) {
		createPort(name, null);
	}

	protected void createOutputPort(String name, Format format) {
		createPort(name, format);
	}

	protected void removePort(String name) {
		supervisor.removePort(this, name);
	}

	/**
	 * Update the properties from a BaseNodeData instance.
	 * Merges the instance with the current properties.
	 * Usage: Create a new instance of BaseNodeData with only the changed fields set and send it into this method.
	 * @param instance A BaseNodeData-subclass instance with the fields being updated set
	 */
	public void updateProperties(BaseNodeData instance) {
		ClassInstanceMerger.Companion.merge(instance, properties, null);
		supervisor.sendMessageToProcessor(new NodeParameterMessage(node.getID(), instance));
		supervisor.sendMessageToUI(new NodeParameterMessage(node.getID(), instance));

		Map<String,?> data = mapper.toMap(properties);
		node.properties.clear();
		node.properties.putAll(data);
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

		if (!supervisor.restoring)
			onCreate();
	}

	void processData(NodeDataMessage data) {
		onData(data);
	}

	/**
	 * Only used by the Supervisor.
	 */
	void setInfo(String id, Supervisor supervisor, Node node) {
		this.id = id;
		this.supervisor = supervisor;
		this.node = node;

		env = supervisor.env;
		logicnode_list = supervisor.logicnode_list;
		netlist = ((Environment) supervisor.env).project.getNetList();

		// Load data
		mapper = UtilKt.getMapperForLogicDataClass(getClass());
		properties = (BaseNodeData) mapper.toObject(node.properties);
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
	 */
	protected void sendMessageToUI(ElasticMessage message) {
		supervisor.sendMessageToUI(message);
	}

	/**
	 * Send message to processor.
	 */
	protected void sendMessageToProcessor(ElasticMessage message) {
		supervisor.sendMessageToProcessor(message);
	}

	private BaseLogicNode getParent() {
		String parentId = properties.getParent();
		if (parentId != null)
			return supervisor.logicnode_list.get(parentId);

		return null;
	}
}