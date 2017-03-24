package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.pack.PackDict;

/**
 * This is the "central control". All node-related messages from UI and processor is sent into this class.
 * The LogicNodes then validates and forwards messages to the processor.
 * 
 * Message-wise, we stand in between the UI (frontend) and the processor.
 */
public class Supervisor {
	public interface Handler {
		public void sendMessageToUI(Postmaster.Message message);
		public void sendMessageToProcessor(Postmaster.Message message);

		/**
		 * Called when processing a frame has been finished.
		 */
		public void onProcessDone();
	}

	private final Object PROCESS_LOCK = new Object();

	private final Handler handler;
	final LogicEnvironment env;

	//NetList netlist; // This is the main NetList
	final LogicNodeList logicnode_list;
	private Postmaster while_processing_queue = new Postmaster();
	private volatile boolean is_processing; // Set to true when LogicNodes (and nodes) are processing, as we then can not take any messages

	public Supervisor(LogicEnvironment env, Handler handler) {
		this.env = env;
		this.handler = handler;

		logicnode_list = new LogicNodeList(this);
	}

	private void createNode(String node_id, String name, Integer version) {
		logicnode_list.getLogicNodeClass(name, version); // Will throw exception if node is not found

		Node node;
		if(node_id == null)
			node = ((Environment)env).project.getNetList().createNode();
		else
			node = ((Environment)env).project.getNetList().createNode(node_id);

		node.properties.put("name", name);
		node.properties.put("version", version);

		logicnode_list.createLogicNode(node);
	}

	public void handleMessageFromUI(Postmaster.Message message) {
		synchronized (PROCESS_LOCK) {
			if(is_processing) {
				while_processing_queue.send(message);
			} else {
				executeMessageFromUI(message);
			}
		}
	}

	private void executeMessageFromUI(Postmaster.Message message) {
		synchronized (PROCESS_LOCK) {
			NetList netlist = ((Environment)env).project.getNetList();

			if(message instanceof CreateNodeMessage) {
				CreateNodeMessage m = (CreateNodeMessage)message;
				createNode(m.node_id, m.name, m.version);
				return;

			} else if(message instanceof NodeParameterMessage) {
				NodeParameterMessage m = (NodeParameterMessage)message;

				NetListMessages.apply(netlist, message); // Apply it already here, and allow the logicnode to change it back

				logicnode_list.get(m.node_id).onParameterChange(m.key, m.value);

				return;

			} else if(message instanceof NodeConnectMessage) { // Notifies LogicNodes about changing of connections
				NodeConnectMessage m = (NodeConnectMessage)message;
				NodeProperties np = new NodeProperties(netlist);

				// Validate the connection
				boolean output_a = np.isOutput(netlist.getPort(m.node_a, m.port_a));
				boolean output_b = np.isOutput(netlist.getPort(m.node_b, m.port_b));

				if(output_a == output_b)
					return; // Only inputs and outputs can be connected

				if(m.node_a.equals(m.node_b))
					return; // Node can not be connected to itself

				if(!output_a && netlist.getConnections(netlist.getNode(m.node_a), m.port_a).size() > 0)
					return; // Input ports can only have 1 line connected

				if(!output_b && netlist.getConnections(netlist.getNode(m.node_b), m.port_b).size() > 0)
					return; // Input ports can only have 1 line connected

				logicnode_list.get(m.node_a).notifyConnect(m.port_a);
				logicnode_list.get(m.node_b).notifyConnect(m.port_b);

				NodeConnectMessage connect_message = new NodeConnectMessage(m.node_a, m.port_a, m.node_b, m.port_b);
				handler.sendMessageToUI(connect_message); // Acknowledge connection
				handler.sendMessageToProcessor(connect_message);

			} else if(message instanceof NodeDisconnectMessage) { // Notifies LogicNodes about changing of connections
				NodeDisconnectMessage m = (NodeDisconnectMessage)message;
				logicnode_list.get(m.node_a).notifyDisconnect(m.port_a);
				logicnode_list.get(m.node_b).notifyDisconnect(m.port_b);

				NodeDisconnectMessage disconnect_message = new NodeDisconnectMessage(m.node_a, m.port_a, m.node_b, m.port_b);
				handler.sendMessageToUI(disconnect_message); // Acknowledge disconnection
				handler.sendMessageToProcessor(disconnect_message); // Acknowledge disconnection

			} else if(message instanceof RemoveNodeMessage) {
				RemoveNodeMessage m = (RemoveNodeMessage)message;
				logicnode_list.get(m.node_id).onRemove();
				logicnode_list.remove(m.node_id);
				handler.sendMessageToUI(new RemoveNodeMessage(m.node_id));
				handler.sendMessageToProcessor(new RemoveNodeMessage(m.node_id));

			} else if(message instanceof ProcessMessage) {
				doProcessFrame((ProcessMessage)message);
			}

			// Apply the changes to the NetList itself
			NetListMessages.apply(netlist, message);
		}

		// XXX Should we handle adding and removing of nodes here? Hmm.
		// Connecting/disconnect should probably not be handled here in any way, other than notifying the LogicNode about it. Yes.
	}

	/**
	 * Used by BaseLogicNode to send messages. 
	 */
	void sendMessageToUI(Postmaster.Message message) {
		handler.sendMessageToUI(message);
	}

	/**
	 * Used by BaseLogicNode to send messages.
	 */
	void sendMessageToProcessor(Postmaster.Message message) {
		handler.sendMessageToProcessor(message);
	}

	/**
	 * Messages sent from processing should be sent into this function.
	 */
	public void handleResponseFromProcessor(ProcessMessage message) {
		synchronized (PROCESS_LOCK) {
			NetList netlist = ((Environment)env).project.getNetList();

			if(!is_processing)
				throw new RuntimeException("Should not happen"); // Got response from processor without requesting it

			// Call all LogicNodes to work on the frame
			for(Node node : netlist.getNodes()) {
				BaseLogicNode bln = logicnode_list.get(node.getID());
				bln.onFinishFrame((PackDict)message.dict.data.get(node.getID()));
			}

			handler.onProcessDone();

			// Execute all messages that are waiting due to LogicNode and processor processing a frame previously
			is_processing = false;
			Postmaster.Message m;
			while((m = while_processing_queue.receive()) != null) {
				handleMessageFromUI(m);
			}
		}
	}

	private void doProcessFrame(ProcessMessage message) {
		System.out.println("processing");
		NetList netlist = ((Environment)env).project.getNetList();
		if(is_processing)
			throw new RuntimeException("Already processing");

		is_processing = true;

		// Build a new ProcessMessage with data from the logic nodes to the processor.
		ProcessMessage m = new ProcessMessage();

		for(Node n : netlist.getNodes()) {
			BaseLogicNode bln = logicnode_list.get(n.getID());

			PackDict p = new PackDict();

			if(!bln.inited) {
				bln.inited = true;
				bln.onInit();
			}

			bln.onPrepareFrame(p);

			m.dict.data.put(n.getID(), p);
		}

		handler.sendMessageToProcessor(m); // Forward process message to processor
	}
}
