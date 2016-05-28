package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

public class Traverser {
	private final NetList netlist;
	private final Util util;

	public Traverser(NetList netlist) {
		this.netlist = netlist;
		this.util = new Util(netlist);
	}

	/**
	 * Retrieves all the nodes that has no connections to the left, that are connected to the node.
	 * These are typically "input"-nodes that should generate data without relaying on input data.
	 */
	public List<Node> getLeftMost(Node node) {
		List<Node> result = new ArrayList<>();
		List<Node> checked = new ArrayList<>();
		Stack<Node> queue = new Stack<>();

		queue.add(node);

		while(!queue.isEmpty()) {
			Node n = queue.pop();
			checked.add(n);

			boolean has_left_connections = false;

			for(String p : util.getInputPorts(n)) {
				List<Line> connections = netlist.getConnections(n, p);

				for(Line l : connections) {
					has_left_connections = true;

					if(!checked.contains(l.node_a))
						queue.add(l.node_a);

					if(!checked.contains(l.node_b))
						queue.add(l.node_b);
				}
			}

			if(!has_left_connections)
				result.add(n);
		}

		return result;
	}

	/**
	 * 
	 */
	public List<Node> getRightMost(Node node) {
		List<Node> result = new ArrayList<>();
		List<Node> checked = new ArrayList<>();
		Stack<Node> queue = new Stack<>();

		queue.add(node);

		while(!queue.isEmpty()) {
			Node n = queue.pop();
			checked.add(n);

			boolean has_right_connections = false;

			for(String p : util.getOutputPorts(n)) {
				List<Line> connections = netlist.getConnections(n, p);

				for(Line l : connections) {
					has_right_connections = true;

					if(!checked.contains(l.node_a))
						queue.add(l.node_a);

					if(!checked.contains(l.node_b))
						queue.add(l.node_b);
				}
			}

			if(!has_right_connections)
				result.add(n);
		}

		return result;
	}
	
	/**
	 * Gets all the groups in the NetList.
	 * A group is nodes that are connected to each other.
	 */
	public List<NetList> getGroups() {
		List<Node> remaining_nodes = netlist.getNodes();

		List<NetList> result = new ArrayList<>();

		while(!remaining_nodes.isEmpty()) {
			
		}

		return result;
	}

	/**
	 * Retrieves all the nodes that are directly or indirectly connected to the
	 * node.
	 */
	public List<Node> getAllInGroup(Node node) {
		List<Node> result = new ArrayList<>();
		Stack<Node> to_check = new Stack<>();

		to_check.add(node);

		while(!to_check.isEmpty()) {
			node = to_check.pop();
			result.add(node);

			for(String port : netlist.getPorts(node)) {
				for(Line line : netlist.getConnections(node, port)) {
					if(!result.contains(line.node_a) && !to_check.contains(line.node_a)) {
						to_check.add(line.node_a);
					} else if(!result.contains(line.node_b) && !to_check.contains(line.node_b)) {
						to_check.add(line.node_b);
					}
				}
			}
		}

		return result;
	}
}
