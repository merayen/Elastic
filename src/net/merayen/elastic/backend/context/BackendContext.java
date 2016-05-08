package net.merayen.elastic.backend.context;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.architectures.Architecture;
import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.nodes.LogicNodeList;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Serializer;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

/**
 * Glues together NetList, MainNodes and the processing backend (architecture)
 */
public class BackendContext {
	private NetList netlist;
	private LogicNodeList logicnode_list;
	private Dispatch dispatch;
	//private final Postmaster to_backend;
	private final Postmaster from_backend;

	private BackendContext() {
		//to_backend = new Postmaster();
		from_backend = new Postmaster();
	}

	/**
	 * Create a new default context, and starts it automatically.
	 * Remember to call end()!
	 */
	public static BackendContext create() {
		BackendContext context = new BackendContext();
		context.netlist = new NetList();
		context.dispatch = new Dispatch(Architecture.LOCAL, new Dispatch.Handler() {
			@Override
			public void onMessage(Postmaster.Message message) {
				
			}
		});

		context.dispatch.launch(context.netlist, 8); // TODO 8? Make it customizable/load it from somewhere

		context.logicnode_list = new LogicNodeList(context.netlist, new LogicNodeList.IHandler() {
			@Override
			public void sendMessageToUI(net.merayen.elastic.util.Postmaster.Message message) { // Message sent from LogicNodes to the UI
				context.from_backend.send(message);
			}

			@Override
			public void sendMessageToBackend(net.merayen.elastic.util.Postmaster.Message message) { // Messages sent further into the backend, from the LogicNodes
				if(message instanceof NodeParameterMessage) { // Update our NetList with the new value
					NodeParameterMessage m = (NodeParameterMessage)message;
					Node node = context.netlist.getNodeByID(((NodeParameterMessage) message).node_id);
					node.properties.put(m.key, m.value);
				}
				context.dispatch.executeMessage(message);
			}
		});

		return context;
	}

	/**
	 * Load from a dump.
	 */
	public static BackendContext load(JSONObject dump) {
		return null; // TODO
	}

	public void executeMessage(Postmaster.Message message) {
		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			logicnode_list.createNode(m.name, m.version);
		}

		else if(message instanceof NodeConnectMessage) {
			NodeConnectMessage m = (NodeConnectMessage)message;

			netlist.connect(m.node_a, m.port_a, m.node_b, m.port_b);
			logicnode_list.handleMessageFromUI(message);
			dispatch.executeMessage(message);

			from_backend.send(message); // Send back to UI to acknowledge connect
		}

		else if(message instanceof NodeDisconnectMessage) {
			NodeDisconnectMessage m = (NodeDisconnectMessage)message;
			netlist.disconnect(m.node_a, m.port_a, m.node_b, m.port_b);
			debug();
			logicnode_list.handleMessageFromUI(message);
			dispatch.executeMessage(message);

			from_backend.send(message); // Send back to UI to acknowledge disconnect
		}

		else if(message instanceof NodeParameterMessage) {
			logicnode_list.handleMessageFromUI(message); // Gets reviewed by LogicNode, which again may edit and forward the Message further into the backend
		}
	}

	public Postmaster.Message receiveFromBackend() {
		return from_backend.receive();
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("netlist", Serializer.dump(netlist));
		return result;
	}

	public LogicNodeList getLogicNodeList() {
		return logicnode_list;
	}

	public NetList getNetList() {
		return netlist.copy();
	}

	public void removeNode(String node_id) {
		logicnode_list.removeNode(node_id);
	}

	private void debug() {
		System.out.println(Serializer.dump(netlist));
	}

	public void end() {
		dispatch.stop();
	}
}
