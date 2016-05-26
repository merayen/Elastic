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
	 * Walk from left to right, from *node*.
	 */
	/*public static void walk(NetList netlist, Node node, Inspector inspector) {
		
	}*/
}
