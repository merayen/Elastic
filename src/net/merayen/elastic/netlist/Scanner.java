package net.merayen.elastic.netlist;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Helper class to retrieve nodes from the NetList
 */
public class Scanner {

	/**
	 * Finds all the connected nodes from this node.
	 */
	public static List<Node> getAllInGroup(NetList netlist, Node node) {
		List<Node> result = new ArrayList<>();

		Deque<Node> to_scan = new ArrayDeque<>();
		to_scan.add(node);

		while(to_scan.size() > 0) {
			Node n = to_scan.pop();
			for(String p : n.getPorts()) {
				for(Line l : netlist.getConnections(n, p)) {
					if(!result.contains(l.node_a)) {
						result.add(l.node_a);
						to_scan.add(l.node_a);
					}

					if(!result.contains(l.node_b)) {
						result.add(l.node_b);
						to_scan.add(l.node_b);
					}
				}
			}
		}

		return result;
	}
}
