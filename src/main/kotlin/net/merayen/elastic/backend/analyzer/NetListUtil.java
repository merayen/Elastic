package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

import java.util.ArrayList;
import java.util.List;

public class NetListUtil {
	private final NetList netlist;
	private final NodeProperties node_properties;

	public NetListUtil(NetList netlist) {
		this.netlist = netlist;
		this.node_properties = new NodeProperties(netlist);
	}

	/**
	 * Retrieves the top-most node, which will be a group-node.
	 * Throws exception if not.
	 */
	public Node getTopGroupNode() {
		List<Node> result = new ArrayList<>();
		for (Node node : netlist.getNodes())
			if (node_properties.getParent(node) == null)
				result.add(node);

		if (result.size() != 1)
			throw new RuntimeException("Expected 1 and only 1 top-node");

		if (!node_properties.getName(result.get(0)).equals("group"))
			throw new RuntimeException("Expected top-most node to be a group-node");

		return result.get(0);
	}

	public Node getParent(Node node) {
		String id = node_properties.getParent(node);
		if (id != null)
			return netlist.getNode(id);

		return null;
	}

	public List<Node> getChildren(Node node) {
		List<Node> result = new ArrayList<>();

		for (Node n : netlist.getNodes())
			if (node.getID().equals(node_properties.getParent(n)))
				result.add(n);

		return result;
	}

	public List<Node> getChildrenDeep(Node node) {
		List<Node> children = getChildren(node);

		List<Node> result = new ArrayList<>(children);

		for (Node n : children)
			result.addAll(getChildrenDeep(n));

		return result;
	}
}
