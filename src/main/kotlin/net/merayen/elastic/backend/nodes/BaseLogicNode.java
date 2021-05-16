package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.backend.analyzer.NetListUtil;
import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.context.JavaBackend;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.ClassInstanceMerger;
import net.merayen.elastic.util.JSONObjectMapper;
import net.merayen.elastic.util.NetListMessages;

import java.util.Map;

public abstract class BaseLogicNode {
	private String id; // Same ID as the one in NetList
	private Supervisor supervisor;
	Node node; // NetList-node that this LogicNode represents
	private NetList netlist;
	private NetListUtil netListUtil = new NetListUtil(netlist);
	private NodeProperties nodeProperties = new NodeProperties(netlist);

	private JSONObjectMapper mapper;
	public BaseNodeProperties properties;

	/**
	 * Called every time the LogicNode class is created (either by creating a new node or loading from existing node).
	 */
	protected abstract void onInit();

	/**
	 * Called when a parameter change has occurred.
	 * This could be the UI changing a parameter, or a restore loading parameters.
	 * Modify value if needed and call set(...) to acknowledge, which will send it to UI and the backend.
	 */
	protected abstract void onParameterChange(BaseNodeProperties instance);

	/**
	 * Data received from UI. Data is not meant for storing, and is usually for streaming etc
	 */
	protected abstract void onData(NodeDataMessage data);

	protected abstract void onConnect(String port); // Port was not connected, but is now connected

	protected abstract void onDisconnect(String port); // Port was connected, but has no more connections

	/**
	 * Called when node is being deleted.
	 */
	protected abstract void onRemove();

	/**
	 * Called when a ProcessMessage()-request has been received. All LogicNodes must prepare data to be sent to processor.
	 * Change the data-argument directly.
	 */
	protected InputFrameData onPrepareFrame() {
		return new InputFrameData(id);
	}

	/**
	 * Call this to create a port.
	 * Only LogicNodes are able to create ports.
	 */
	private void createPort(String name, Format format) {
		boolean output = format != null;

		if (name == null || name.length() == 0)
			throw new RuntimeException("Invalid port name");

		// Everything OK
		ElasticMessage message = new CreateNodePortMessage(id, name, output, format); // TODO rename to chain_ident

		NetListMessages.INSTANCE.apply(netlist, message); // Apply the port to the NetList

		supervisor.sendToUI(message);
	}

	protected void createInputPort(String name) {
		createPort(name, null);
	}

	protected void createOutputPort(String name, Format format) {
		createPort(name, format);
	}

	/**
	 * Retrieves the format of a port. Returns null if port does not exist.
	 */
	protected Format getInputPortFormat(String name) {
		return netListUtil.getInputPortFormat(node, name);
	}

	protected Format getOutputPortFormat(String name) {
		return netListUtil.getOutputPortFormat(node, name);
	}

	protected void removePort(String name) {
		supervisor.removePort(this, name);
	}

	/**
	 * Update the properties from a BaseNodeData instance.
	 * Merges the instance with the current properties.
	 * Usage: Create a new instance of BaseNodeData with only the changed fields set and send it into this method.
	 *
	 * @param instance A BaseNodeData-subclass instance with the fields being updated set
	 */
	public void updateProperties(BaseNodeProperties instance) {
		ClassInstanceMerger.Companion.merge(instance, properties, null);
		supervisor.sendToUI(new NodePropertyMessage(node.getID(), instance));

		Map<String, ?> data = mapper.toMap(properties);
		node.properties.clear();
		node.properties.putAll(data);
	}

	protected void sendDataToUI(NodeDataMessage data) {
		sendToUI(data);
	}

	protected String[] getPorts() {
		return netlist.getPorts(node);
	}

	void create(String name, Integer version, String parent) {
		CreateNodeMessage m = new CreateNodeMessage(id, name, version, parent);
		supervisor.sendToUI(m); // Acknowledges creation of Node

		onInit();
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

		netlist = supervisor.getEnv().getProject().getNetList();

		// Load data
		mapper = UtilKt.getMapperForLogicPropertiesClass(getClass());
		properties = (BaseNodeProperties) mapper.toObject(node.properties);
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

	protected JavaBackend.Environment getEnv() { // TODO soon: Try to remove this one?
		return supervisor.getEnv();
	}

	/**
	 * Send message to UI (frontend).
	 */
	protected void sendToUI(ElasticMessage message) {
		supervisor.sendToUI(message);
	}

	protected void sendToDSP(NodeDataMessage message) {
		supervisor.sendToDSP(message);
	}

	private BaseLogicNode getParent() {
		String parentId = properties.getParent();
		if (parentId != null)
			return supervisor.getLogicnode_list().get(parentId);

		return null;
	}
}