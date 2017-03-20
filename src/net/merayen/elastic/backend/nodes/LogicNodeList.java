package net.merayen.elastic.backend.nodes;

import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

class LogicNodeList {
	private static final String CLASS_PATH = "net.merayen.elastic.backend.logicnodes.list.%s_%d.%s";

	private final Supervisor supervisor;
	private final Map<String, BaseLogicNode> nodes = new HashMap<>();

	public LogicNodeList(Supervisor supervisor) {
		this.supervisor = supervisor;
	}

	void createLogicNode(Node node) {
		NetList netlist = ((Environment)supervisor.env).project.getNetList();

		NodeProperties np = new NodeProperties(netlist);
		String name = np.getName(node);
		Integer version = np.getVersion(node);
		String group = np.getGroup(node);

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

		logicnode.setInfo(node.getID(), supervisor, node);

		nodes.put(node.getID(), logicnode);

		logicnode.create(name, version, group);
	}

	void remove(String node_id) {
		nodes.remove(node_id);
	}

	BaseLogicNode get(String node_id) {
		return nodes.get(node_id);
	}

	@SuppressWarnings("unchecked")
	Class<? extends BaseLogicNode> getLogicNodeClass(String name, Integer version) {
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
