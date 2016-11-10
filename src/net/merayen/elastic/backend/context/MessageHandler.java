package net.merayen.elastic.backend.context;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.nodes.LogicNodeList;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.CreateNodeMessage;
import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.system.intercom.NodeConnectMessage;
import net.merayen.elastic.system.intercom.NodeDisconnectMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.ResetNetListMessage;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.NetListMessages;

/**
 * Helper class for BackendContext.
 * Executes messages sent from UI and backend
 */
class MessageHandler {
	private final BackendContext backend_context;

	MessageHandler(BackendContext bc) {
		this.backend_context = bc;
	}

	/**
	 * Messages sent from LogicNode further into backend is handled here.
	 */
	void handleFromLogicToBackend(Postmaster.Message message) {
		if(message instanceof NodeParameterMessage) { // Update our NetList with the new value
			NodeParameterMessage m = (NodeParameterMessage)message;
			Node node = backend_context.netlist.getNode(m.node_id);
			node.properties.put(m.key, m.value);

		} else if(message instanceof CreateNodeMessage) {
			
		} else {
			NetListMessages.apply(backend_context.netlist, message);
		}

		backend_context.dispatch.executeMessage(message);
	}

	void handleFromLogicToUI(Postmaster.Message message) {
		backend_context.from_backend.send(message);
	}

	/**
	 * Handles messages sent from UI.
	 * TODO Should we accumulate messages here, wait like 10ms, run the analyzer if necessary and then spread the messages around again? This is to not call the analyzer for every message
	 */
	void handleFromUI(Postmaster.Message message) {
		LogicNodeList logicnode_list = backend_context.logicnode_list;
		NetList netlist = backend_context.netlist;
		NodeProperties np = backend_context.node_properties;
		Dispatch dispatch = backend_context.dispatch;
		Postmaster from_backend = backend_context.from_backend;

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			logicnode_list.createNode(m.name, m.version); // 
		}

		else if(message instanceof NodeConnectMessage) {
			NodeConnectMessage m = (NodeConnectMessage)message;

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

			NetListMessages.apply(netlist, message);

			logicnode_list.handleMessageFromUI(message);
			dispatch.executeMessage(message); // Notify the architecture too
			from_backend.send(message); // Send back to UI to acknowledge connect
		}

		else if(message instanceof NodeDisconnectMessage) {
			NodeDisconnectMessage m = (NodeDisconnectMessage)message;
			netlist.disconnect(m.node_a, m.port_a, m.node_b, m.port_b);

			logicnode_list.handleMessageFromUI(message);
			dispatch.executeMessage(message);
			from_backend.send(message); // Send back to UI to acknowledge disconnect
		}

		else if(message instanceof NodeParameterMessage) {
			logicnode_list.handleMessageFromUI(message); // Gets reviewed by LogicNode, which again may edit and forward the Message further into the backend
		}

		else if(message instanceof ProcessMessage) {
			dispatch.executeMessage(message);
		}

		else if(message instanceof NetListRefreshRequestMessage) {
			NetListRefreshRequestMessage m = (NetListRefreshRequestMessage)message;
			from_backend.send(new ResetNetListMessage(m.group_id)); // We must clear the NetList in UI before restoring it again

			for(Postmaster.Message mess :  NetListMessages.disassemble(netlist, m.group_id))
				from_backend.send(mess);
		}
	}
}
