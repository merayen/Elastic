package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

import java.util.HashMap;
import java.util.Map;

import static net.merayen.elastic.backend.nodes.UtilKt.createLogicNode;

public class LogicNodeList {
	private final Supervisor supervisor;
	private final Map<String, BaseLogicNode> nodes = new HashMap<>();

	public LogicNodeList(Supervisor supervisor) {
		this.supervisor = supervisor;
	}

	void addAsLogicNode(Node node) {
		NetList netlist = supervisor.getEnv().getProject().getNetList();

		NodeProperties np = new NodeProperties(netlist);
		String name = np.getName(node);
		int version = np.getVersion(node);

		BaseLogicNode logicnode = createLogicNode(name , version);

		logicnode.setInfo(node.getID(), supervisor, node);

		nodes.put(node.getID(), logicnode);

		logicnode.create(name, version, np.getParent(node));
	}

	void remove(String node_id) {
		nodes.remove(node_id);
	}

	BaseLogicNode get(String node_id) {
		return nodes.get(node_id);
	}

	public void clear() {
		nodes.clear();
	}
}
