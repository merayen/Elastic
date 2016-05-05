package net.merayen.elastic.backend.nodes;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

public class LogicNodeList {
	private static final String CLASS_PATH = "net.merayen.elastic.backend.nodes.list.%s_%d.%s";
	private final List<BaseLogicNode> nodes = new ArrayList<>();
	private final NetList netlist;

	public LogicNodeList(NetList netlist) {
		this.netlist = netlist;
		for(Node node : netlist.getNodes()) {
			BaseLogicNode logicnode = createLogicNode(node);
		}
	}

	public String createNode(String name, Integer version) {
		getLogicNodeClass(name, version); // Will throw exception if node is not found

		Node node = netlist.createNode();
		node.properties.put("name", name);
		node.properties.put("version", version);

		BaseLogicNode logicnode = createLogicNode(node);
		nodes.add(logicnode);

		logicnode.onCreate();

		return node.getID();
	}

	private BaseLogicNode createLogicNode(Node node) {
		Class<? extends BaseLogicNode> cls = getLogicNodeClass(
			(String)node.properties.get("name"),
			(Integer)node.properties.get("version")
		);

		BaseLogicNode mainnode;
		try {
			mainnode = cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		mainnode.setInfo(node.getID(), netlist, node);

		return mainnode;
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
}
