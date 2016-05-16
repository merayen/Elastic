package net.merayen.elastic.netlist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class NetList {
	@SuppressWarnings("serial") public static class NetListException extends RuntimeException {}
	@SuppressWarnings("serial") public static class NodeNotFound extends NetListException {}
	@SuppressWarnings("serial") public static class PortNotFound extends NetListException {}
	@SuppressWarnings("serial") public static class ConnectionNotFound extends NetListException {}
	@SuppressWarnings("serial") public static class AlreadyConnected extends NetListException {}

	final List<Node> nodes = new ArrayList<Node>();
	final List<Line> lines = new ArrayList<Line>();

	public NetList() {
		
	}

	/**
	 * Called when copying a NetList.
	 * Initializes directly from the parameter and does deep copy.
	 */
	private NetList(List<Node> nodes, List<Line> lines) {
		for(Node node : nodes)
			this.nodes.add(node.copy());

		for(Line line : lines) {
			Node node_a = null;
			Node node_b = null;

			for(Node node : this.nodes) {
				if(node.equals(line.node_a))
					node_a = node;
				if(node.equals(line.node_b))
					node_b = node;
			}

			if(node_a == null || node_b == null)
				throw new RuntimeException("Should not happen");

			this.lines.add(new Line(node_a, line.port_a, node_b, line.port_b));
		}
	}

	public synchronized Node createNode() {
		Node n = new Node();
		nodes.add(n);
		return n;
	}

	public void removeNode(String node_id) {
		removeNode(getNodeByID(node_id));
	}

	public synchronized void removeNode(Node node) {
		if(!nodes.contains(node))
			throw new NodeNotFound();

		// Disconnect all lines for this node
		for(String p : node.ports.keySet())
			disconnectAll(node, p);

		while(nodes.remove(node)); // Should only be one
	}

	public synchronized List<Node> getNodes() {
		return new ArrayList<>(nodes);
	}

	public synchronized List<Line> getLines() {
		return new ArrayList<>(lines);
	}

	public synchronized Node getNodeByID(String id) {
		for(Node node : nodes)
			if(node.id.equals(id))
				return node;

		throw new RuntimeException(String.format("Node by id %s was not found", id));
	}

	public synchronized void connect(String node_a, String port_a, String node_b, String port_b) {
		connect(getNodeByID(node_a), port_a, getNodeByID(node_b), port_b);
	}

	public synchronized void connect(Node node_a, String port_a, Node node_b, String port_b) {
		if(node_a == node_b)
			throw new RuntimeException("Node can not connect to itself");

		if(!node_a.ports.containsKey(port_a) || !node_b.ports.containsKey(port_b))
			throw new PortNotFound();

		if(hasConnection(node_a, port_a, node_b, port_b))
			throw new AlreadyConnected();

		Line line = new Line(node_a, port_a, node_b, port_b);
		lines.add(line);
	}

	public synchronized void disconnect(String node_a, String port_a, String node_b, String port_b) {
		disconnect(getNodeByID(node_a), port_a, getNodeByID(node_b), port_b);
	}

	public synchronized void disconnect(Node node_a, String port_a, Node node_b, String port_b) {
		if(!hasConnection(node_a, port_a, node_b, port_b))
			throw new ConnectionNotFound();

		for(int i = lines.size() - 1; i >= 0; i--) {
			Line l = lines.get(i);
			if(l.node_a == node_a && l.node_b == node_b) {
				if(l.port_a.equals(port_a) && l.port_b.equals(port_b))
					lines.remove(i);
			} else if(l.node_a == node_b && l.node_b == node_a) {
				if(l.port_a.equals(port_b) && l.port_b.equals(port_a))
					lines.remove(i);
			}
		}
	}

	public synchronized void disconnectAll(Node node, String port) {
		for(Line l : getConnections(node, port))
			lines.remove(l);
	}

	public synchronized List<Line> getConnections(Node node, String port) {
		List<Line> result = new ArrayList<>();

		for(Line l : lines)
			if(l.node_a == node && l.port_a.equals(port))
				result.add(l);
			else if(l.node_b == node && l.port_b.equals(port))
				result.add(l);

		return result;
	}

	public NetList copy() {
		return new NetList(nodes, lines);
	}

	/*
	 * Checks if a port is connected at all.
	 */
	/*public synchronized boolean isConnected( p) {
		for(Line l : lines)
			if(l.a == p || l.b == p)
				return true;
		return false;
	}*/

	/*public synchronized ArrayList<Line> getLines() {
		return new ArrayList<Line>(lines);
	}*/

	private synchronized boolean hasConnection(Node node_a, String port_a, Node node_b, String port_b) {
		for(Line l : getConnections(node_a, port_a))
			if((l.node_a == node_a && l.port_a == port_a) && (l.node_b == node_b && l.port_b.equals(port_b)))
				return true;
			else if((l.node_a == node_b && l.port_a == port_b) && (l.node_b == node_a && l.port_b.equals(port_a)))
				return true;

		return false;
	}
}
