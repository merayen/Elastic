package net.merayen.elastic.backend.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.analyzer.NetListUtil;
import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.script.interpreter.Any;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;

/**
 * This is the "central control". All node-related messages from UI and processor is sent into this class.
 * The LogicNodes then validates and forwards messages to the processor.
 * <p>
 * Message-wise, we stand in between the UI (frontend) and the processor.
 */
public class Supervisor {
	public interface Handler {
		void sendMessageToUI(Object message);

		void sendMessageToProcessor(Object message);

		/**
		 * Called when processing a frame has been finished.
		 */
		void onProcessDone();
	}

	private final Handler handler;
	final LogicEnvironment env;

	//NetList netlist; // This is the main NetList
	final LogicNodeList logicnode_list;
	private Postmaster while_processing_queue = new Postmaster();
	private volatile boolean is_processing; // Set to true when LogicNodes (and nodes) are processing, as we then can not take any messages
	volatile boolean restoring; // Set to true when receiving NetListRefreshMessage, meaning that no events should be called
	final NodeProperties node_properties;

	public Supervisor(LogicEnvironment env, Handler handler) {
		this.env = env;
		this.handler = handler;

		node_properties = new NodeProperties(((Environment) env).project.getNetList());
		logicnode_list = new LogicNodeList(this);
	}

	private void createNode(String node_id, String name, Integer version, String parent) {
		logicnode_list.getLogicNodeClass(name, version); // Will throw exception if node is not found

		Node node;
		if (node_id == null)
			node = ((Environment) env).project.getNetList().createNode();
		else
			node = ((Environment) env).project.getNetList().createNode(node_id);

		node_properties.setName(node, name);
		node_properties.setVersion(node, version);
		node_properties.setParent(node, parent);

		logicnode_list.createLogicNode(node);
	}

	public synchronized void handleMessageFromUI(Object message) {
		if (is_processing) {
			while_processing_queue.send(message);
		} else {
			executeMessageFromUI(message);
		}
	}

	private synchronized void executeMessageFromUI(Object message) {
		NetList netlist = ((Environment) env).project.getNetList();

		if (message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage) message;
			createNode(m.node_id, m.name, m.version, m.parent);

		} else if (message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage) message;
			NetListMessages.INSTANCE.apply(netlist, message); // Apply it already here, and allow the logicnode to change it back

			BaseLogicNode logicNode = logicnode_list.get(m.node_id);
			Map<String, Class<?>> parameterRegistry = logicNode.getParameterRegistry();

			if (parameterRegistry != null) {
				Class<?> klass = parameterRegistry.get(m.key);
				if (klass != null) {
					if (!m.value.getClass().isAssignableFrom(klass)) {
						System.out.printf("Skipped invalid parameter %s for node %s (%s) due to invalid type. (Expected %s, got %s)\n", m.key, m.node_id, logicNode.getClass().getName(), klass.getSimpleName(), m.value.getClass().getSimpleName());
						return;
					}
				} else {
					System.out.printf("Skipped invalid parameter %s for node %s (%s) as it isn't registered.\n", m.key, m.node_id, logicNode.getClass().getSimpleName());
					return;
				}
			}

			logicNode.onParameterChange(m.key, m.value);

		} else if (message instanceof NodeDataMessage) {
			NodeDataMessage m = (NodeDataMessage) message;
			logicnode_list.get(m.getNodeId()).processData(m);

		} else if (message instanceof NodeConnectMessage) { // Notifies LogicNodes about changing of connections
			NodeConnectMessage m = (NodeConnectMessage) message;
			NodeProperties np = new NodeProperties(netlist);

			// Validate the connection
			boolean output_a = np.isOutput(netlist.getPort(m.node_a, m.port_a));
			boolean output_b = np.isOutput(netlist.getPort(m.node_b, m.port_b));

			if (output_a == output_b)
				return; // Only inputs and outputs can be connected

			if (m.node_a.equals(m.node_b))
				return; // Node can not be connected to itself

			if (!output_a && netlist.getConnections(netlist.getNode(m.node_a), m.port_a).size() > 0)
				return; // Input ports can only have 1 line connected

			if (!output_b && netlist.getConnections(netlist.getNode(m.node_b), m.port_b).size() > 0)
				return; // Input ports can only have 1 line connected

			NodeConnectMessage connect_message = new NodeConnectMessage(m.node_a, m.port_a, m.node_b, m.port_b);
			NetListMessages.INSTANCE.apply(netlist, message);
			handler.sendMessageToUI(connect_message); // Acknowledge connection
			handler.sendMessageToProcessor(connect_message); // Forward to backend

			if (!restoring) {
				logicnode_list.get(m.node_a).notifyConnect(m.port_a);
				logicnode_list.get(m.node_b).notifyConnect(m.port_b);
			}

		} else if (message instanceof NodeDisconnectMessage) { // Notifies LogicNodes about changing of connections
			NodeDisconnectMessage m = (NodeDisconnectMessage) message;
			if (restoring)
				throw new RuntimeException("Node ports can not be disconnected when restoring from a NetList. Requires FinishResetNetListMessage() first");

			NodeDisconnectMessage disconnect_message = new NodeDisconnectMessage(m.node_a, m.port_a, m.node_b, m.port_b);
			NetListMessages.INSTANCE.apply(netlist, message);
			handler.sendMessageToUI(disconnect_message); // Acknowledge disconnection
			handler.sendMessageToProcessor(disconnect_message); // Forward to backend

			logicnode_list.get(m.node_a).notifyDisconnect(m.port_a);
			logicnode_list.get(m.node_b).notifyDisconnect(m.port_b);

		} else if (message instanceof CreateNodePortMessage) {
			if (!restoring)
				throw new RuntimeException("Node ports can only be created for logic nodes when restoring, otherwise logic nodes must create them");

			NetListMessages.INSTANCE.apply(netlist, message);
			handler.sendMessageToUI(message);
			handler.sendMessageToProcessor(message);

		} else if (message instanceof RemoveNodePortMessage) {
			throw new RuntimeException("Not allowed. Only logic nodes can do this action");

		} else if (message instanceof RemoveNodeMessage) {
			RemoveNodeMessage m = (RemoveNodeMessage) message;

			NetListUtil netListUtil = new NetListUtil(netlist);
			List<Node> toRemove = netListUtil.getChildrenDeep(netlist.getNode(m.node_id));

			toRemove.add(netlist.getNode(m.node_id));

			for (Node nodeToDelete : toRemove) {
				if (!restoring)
					logicnode_list.get(nodeToDelete.getID()).onRemove();

				logicnode_list.remove(nodeToDelete.getID());

				NetListMessages.INSTANCE.apply(netlist, new RemoveNodeMessage(nodeToDelete.getID()));
				handler.sendMessageToUI(new RemoveNodeMessage(nodeToDelete.getID()));
				handler.sendMessageToProcessor(new RemoveNodeMessage(nodeToDelete.getID()));
			}

		} else if (message instanceof ProcessMessage) {
			if (restoring)
				throw new RuntimeException("In restore mode. Forgotten to send FinishResetNetListMessage() to end restoring?");

			doProcessFrame((ProcessMessage) message);

		} else if (message instanceof BeginResetNetListMessage) {
			restoring = true;

		} else if (message instanceof FinishResetNetListMessage) {
			restoring = false;
		}
	}

	/**
	 * Used by BaseLogicNode to send messages.
	 */
	void sendMessageToUI(Object message) {
		handler.sendMessageToUI(message);
	}

	/**
	 * Used by BaseLogicNode to send messages.
	 */
	void sendMessageToProcessor(Object message) {
		handler.sendMessageToProcessor(message);
	}

	/**
	 * Messages sent from processing should be sent into this function.
	 */
	public synchronized void handleResponseFromProcessor(ProcessMessage message) {
		NetList netlist = ((Environment) env).project.getNetList();

		if (!is_processing)
			throw new RuntimeException("Should not happen"); // Got response from processor without requesting it

		// Call all LogicNodes to work on the frame
		for (Node node : netlist.getNodes()) {
			BaseLogicNode bln = logicnode_list.get(node.getID());
			bln.onFinishFrame(message.getOutput().get(node.getID()));
		}

		handler.onProcessDone();

		// Execute all messages that are waiting due to LogicNode and processor processing a frame previously
		is_processing = false;
		Object m;
		while ((m = while_processing_queue.receive()) != null)
			handleMessageFromUI(m);

		// Send any statistics report to UI if available
		if (message.getStatisticsReportMessage() != null)
			sendMessageToUI(message.getStatisticsReportMessage());
	}

	void removePort(BaseLogicNode logic_node, String name) {
		NetList netlist = env.project.getNetList();
		if (netlist.getPort(logic_node.node, name) == null)
			throw new RuntimeException(String.format("Port %s does not exist on Node", name));

		// Notify the node(s) connected on the other side that we will disconnect
		for (Line line : netlist.getConnections(logic_node.node, name))
			if (line.node_a.getID().equals(logic_node.getID()))
				logicnode_list.get(line.node_b.getID()).notifyDisconnect(line.port_b);
			else
				logicnode_list.get(line.node_a.getID()).notifyDisconnect(line.port_a);

		netlist.removePort(logic_node.node, name);

		// Notify both ways
		Object message = new RemoveNodePortMessage(logic_node.getID(), name);
		sendMessageToUI(message);
		sendMessageToProcessor(message);
	}

	private void doProcessFrame(ProcessMessage message) {
		NetList netlist = ((Environment) env).project.getNetList();
		if (is_processing)
			throw new RuntimeException("Already processing");

		is_processing = true;

		// Build a new ProcessMessage with data from the logic nodes to the processor.
		ProcessMessage m = new ProcessMessage();

		for (Node n : netlist.getNodes()) {
			BaseLogicNode bln = logicnode_list.get(n.getID());

			if (!bln.inited) {
				bln.inited = true;
				bln.onInit();
			}

			m.getInput().put(n.getID(), bln.onPrepareFrame());
		}

		handler.sendMessageToProcessor(m); // Forward process message to processor
	}
}
