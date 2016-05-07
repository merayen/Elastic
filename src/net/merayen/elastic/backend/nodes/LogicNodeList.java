package net.merayen.elastic.backend.nodes;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

public class LogicNodeList {
	public interface IHandler {
		public void sendMessageToUI(Postmaster.Message message);
		public void sendMessageToBackend(Postmaster.Message message);
	}

	private final IHandler handler;
	private static final String CLASS_PATH = "net.merayen.elastic.backend.nodes.list.%s_%d.%s";
	private final List<BaseLogicNode> nodes = new ArrayList<>();
	private final NetList netlist;

	public LogicNodeList(NetList netlist, IHandler handler) {
		this.handler = handler;
		this.netlist = netlist;
		for(Node node : netlist.getNodes()) {
			createLogicNode(node);
		}
	}

	public String createNode(String name, Integer version) {
		getLogicNodeClass(name, version); // Will throw exception if node is not found

		Node node = netlist.createNode();
		node.properties.put("name", name);
		node.properties.put("version", version);

		createLogicNode(node);

		return node.getID();
	}

	private BaseLogicNode createLogicNode(Node node) {
		String name = (String)node.properties.get("name");
		Integer version = (Integer)node.properties.get("version");

		Class<? extends BaseLogicNode> cls = getLogicNodeClass(
			name,
			version
		);

		BaseLogicNode logicnode;
		try {
			logicnode = cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		logicnode.setInfo(node.getID(), this, node);

		nodes.add(logicnode);

		logicnode.create(name, version);

		return logicnode;
	}

	public BaseLogicNode get(String node_id) {
		for(BaseLogicNode x : nodes)
			if(x.getID().equals(node_id))
				return x;

		return null;
	}

	public void removeNode(String node_id) {
		netlist.removeNode(node_id);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends BaseLogicNode> getLogicNodeClass(String name, Integer version) {
		Class<? extends BaseLogicNode> cls;
		try {
			cls = (Class<? extends BaseLogicNode>)Class.forName(String.format(
				CLASS_PATH,
				name,
				version,
				"LogicNode"
			));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return cls;
	}

	void sendMessageToUI(Postmaster.Message message) {
		handler.sendMessageToUI(message);
	}

	void sendMessageToBackend(Postmaster.Message message) {
		handler.sendMessageToBackend(message);
	}

	public void handleMessageFromUI(Postmaster.Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			get(m.node_id).onMessageFromUI(message);

		} else if(message instanceof NodeConnectMessage) { // Notifies LogicNodes about changing of connections
			NodeConnectMessage m = (NodeConnectMessage)message;
			get(m.node_a).notifyConnect(m.port_a);
			get(m.node_b).notifyConnect(m.port_b);

		} else if(message instanceof NodeDisconnectMessage) { // Notifies LogicNodes about changing of connections
			NodeDisconnectMessage m = (NodeDisconnectMessage)message;
			get(m.node_a).notifyDisconnect(m.port_a);
			get(m.node_b).notifyDisconnect(m.port_b);

		}

		// XXX Should we handle adding and removing of nodes here? Hmm.
		// Connecting/disconnect should probably not be handled here in any way, other than notifying the LogicNode about it. Yes.
	}
}
