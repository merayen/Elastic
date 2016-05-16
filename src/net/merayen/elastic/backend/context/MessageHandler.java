package net.merayen.elastic.backend.context;

import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.backend.nodes.LogicNodeList;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.CreateNodeMessage;
import net.merayen.elastic.system.intercom.CreateNodePortMessage;
import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.system.intercom.NodeConnectMessage;
import net.merayen.elastic.system.intercom.NodeDisconnectMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.ResetNetListMessage;
import net.merayen.elastic.util.Postmaster;

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
	 * Messages sent from LogicNode to further into backend is handled here.
	 */
	void handleFromLogicToBackend(Postmaster.Message message) {
		if(message instanceof NodeParameterMessage) { // Update our NetList with the new value
			NodeParameterMessage m = (NodeParameterMessage)message;
			Node node = backend_context.netlist.getNodeByID(m.node_id);
			node.properties.put(m.key, m.value);
		}

		else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;

			Port port = backend_context.netlist.getNodeByID(m.node_id).createPort(m.port);

			port.properties.put("output", m.output);
			port.properties.put("format", Format.toStrings(m.format));
			port.properties.put("poly_no", m.poly_no);
		}

		backend_context.dispatch.executeMessage(message);
	}

	void handleFromLogicToUI(Postmaster.Message message) {
		backend_context.from_backend.send(message);
	}

	/**
	 * Handles messages sent from UI.
	 */
	void handleFromUI(Postmaster.Message message) {
		LogicNodeList logicnode_list = backend_context.logicnode_list;
		NetList netlist = backend_context.netlist;
		Dispatch dispatch = backend_context.dispatch;
		Postmaster from_backend = backend_context.from_backend;

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			logicnode_list.createNode(m.name, m.version);
		}

		else if(message instanceof NodeConnectMessage) {
			NodeConnectMessage m = (NodeConnectMessage)message;

			boolean output_a = (boolean)netlist.getNodeByID(m.node_a).getPort(m.port_a).properties.get("output");
			boolean output_b = (boolean)netlist.getNodeByID(m.node_b).getPort(m.port_b).properties.get("output");

			if(output_a == output_b)
				return; // Only inputs and outputs can be connected

			if(m.node_a.equals(m.node_b))
				return; // Node can not be connected to itself

			if(!output_a && netlist.getConnections(netlist.getNodeByID(m.node_a), m.port_a).size() > 0)
				return; // Input ports can only have 1 line connected

			if(!output_b && netlist.getConnections(netlist.getNodeByID(m.node_b), m.port_b).size() > 0)
				return; // Input ports can only have 1 line connected
				

			netlist.connect(m.node_a, m.port_a, m.node_b, m.port_b);
			logicnode_list.handleMessageFromUI(message);
			dispatch.executeMessage(message);
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

		else if(message instanceof NetListRefreshRequestMessage) {
			NetListRefreshRequestMessage m = (NetListRefreshRequestMessage)message;
			from_backend.send(new ResetNetListMessage(m.group_id)); // We must clear the NetList in UI before restoring it again

			for(Postmaster.Message mess :  Restore.restore(netlist, m.group_id))
				from_backend.send(mess);
		}
	}
}
