package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	public Node getTopNode() {
		List<Node> result = new ArrayList<>();
		for (Node node : netlist.getNodes())
			if (node_properties.getParent(node) == null)
				result.add(node);

		if (result.size() != 1)
			throw new RuntimeException("Expected 1 and only 1 top-node, but got " + result.size());

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

	/**
	 * Get all ids on nodes that has children.
	 */
	public Set<String> getGroupNodeIds() {
		return netlist.getNodes().stream().map(node_properties::getParent).collect(Collectors.toSet());
	}

	/**
	 * Retrieve all nodes connected to the right side of a node.
	 */
	public Set<Node> getRightNodes(Node node) {
		Set<Node> result = new HashSet<>();

		for (String port : node_properties.getOutputPorts(node)) {
			for (Line line : netlist.getConnections(node, port)) {
				if (line.node_a == node)
					result.add(line.node_b);
				else
					result.add(line.node_a);
			}
		}

		return result;
	}

	public Format getInputPortFormat(Node node, String portName) {
		List<String> ports = node_properties.getInputPorts(node).stream().filter((name) -> name.equals(portName)).collect(Collectors.toList());

		if (ports.isEmpty())
			return null;

		if (ports.size() != 1)
			throw new RuntimeException("A node should not have multiple ports with the same name");

		List<Line> lines = netlist.getConnections(node, portName);

		if (lines.isEmpty())
			return null; // if there is nothing connected to the line, we have no idea what format it is

		if (lines.size() != 1)
			throw new RuntimeException("Inlet should never have more than 1 line connected");

		if (lines.get(0).node_a == node)
			return node_properties.getFormat(netlist.getPort(lines.get(0).node_b, lines.get(0).port_b));
		else
			return node_properties.getFormat(netlist.getPort(lines.get(0).node_a, lines.get(0).port_a));
	}

	public Format getOutputPortFormat(Node node, String portName) {
		List<String> ports = node_properties.getOutputPorts(node).stream().filter((name) -> name.equals(portName)).collect(Collectors.toList());
		if (ports.isEmpty())
			return null;

		if (ports.size() != 1)
			throw new RuntimeException("A node should not have multiple ports with the same name");

		Port port = netlist.getPort(node, ports.get(0));

		return node_properties.getFormat(port);
	}

	public boolean hasInputPort(Node node, String portName) {
		List<String> ports = node_properties.getInputPorts(node).stream().filter((name) -> name.equals(portName)).collect(Collectors.toList());

		if (ports.isEmpty())
			return false;

		if (ports.size() != 1)
			throw new RuntimeException("A node should not have multiple ports with the same name");

		return true;
	}

	public boolean hasOutputPort(Node node, String portName) {
		List<String> ports = node_properties.getOutputPorts(node).stream().filter((name) -> name.equals(portName)).collect(Collectors.toList());
		return !ports.isEmpty();
	}
}
