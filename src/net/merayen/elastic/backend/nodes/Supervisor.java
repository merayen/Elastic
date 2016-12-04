package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.NodeConnectMessage;
import net.merayen.elastic.system.intercom.NodeDisconnectMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.util.Postmaster;

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
	}

	private static final Object PROCESS_LOCK = new Object();

	private final Handler handler;
	final NetList netlist = new NetList(); // This is the main NetList
	final LogicNodeList logicnode_list;
	private Postmaster while_processing_queue = new Postmaster();
	private volatile boolean is_processing; // Set to true when LogicNodes (and nodes) are processing, as we then can not take any messages

	public Supervisor(Handler handler) {
		this.handler = handler;
		logicnode_list = new LogicNodeList(this);
	}

	private void createNode(String name, Integer version) {
		logicnode_list.getLogicNodeClass(name, version); // Will throw exception if node is not found

		Node node = netlist.createNode();
		node.properties.put("name", name);
		node.properties.put("version", version);

		logicnode_list.createLogicNode(node);
	}

	public void handleMessageFromUI(Postmaster.Message message) {
		synchronized (PROCESS_LOCK) {
			if(is_processing)
				while_processing_queue.send(message);
			else
				handleMessageFromUI(message);
		}
	}

	private void executeMessageFromUI(Postmaster.Message message) {
		synchronized (PROCESS_LOCK) {
			if(message instanceof NodeParameterMessage) {
				NodeParameterMessage m = (NodeParameterMessage)message;
				logicnode_list.get(m.node_id).onParameterChange(m);
	
			} else if(message instanceof NodeConnectMessage) { // Notifies LogicNodes about changing of connections
				NodeConnectMessage m = (NodeConnectMessage)message;
				logicnode_list.get(m.node_a).notifyConnect(m.port_a);
				logicnode_list.get(m.node_b).notifyConnect(m.port_b);
	
			} else if(message instanceof NodeDisconnectMessage) { // Notifies LogicNodes about changing of connections
				NodeDisconnectMessage m = (NodeDisconnectMessage)message;
				logicnode_list.get(m.node_a).notifyDisconnect(m.port_a);
				logicnode_list.get(m.node_b).notifyDisconnect(m.port_b);
	
			} else if(message instanceof ProcessMessage) {
				ProcessMessage m = (ProcessMessage)message;
				if(is_processing)
					throw new RuntimeException("Already processing");

				handler.sendMessageToProcessor(message); // Forward process message to processor
			}
		}

		// XXX Should we handle adding and removing of nodes here? Hmm.
		// Connecting/disconnect should probably not be handled here in any way, other than notifying the LogicNode about it. Yes.
	}

	/**
	 * Used by BaseLogicNode to send messages.
	 * Message is sent both ways. 
	 */
	void sendMessage(Postmaster.Message message) {
		handler.sendMessageToProcessor(message);
		handler.sendMessageToUI(message);
	}

	/**
	 * Messages sent from processing should be sent into this function.
	 */
	public void handleResponseFromProcessing(ProcessMessage message) {
		synchronized (PROCESS_LOCK) {
			if(!is_processing)
				throw new RuntimeException("Should not happen"); // Got response from processor without requesting it
	
			// Call all LogicNodes to work on the frame
			// Execute all messages that are waiting due to LogicNode and processor processing a frame previously
			
			is_processing = false;
			Postmaster.Message m;
			while((m = while_processing_queue.receive()) != null) {
				handleMessageFromUI(m);
				if(m instanceof ProcessMessage)
					break;
			}
		}
	}
}
