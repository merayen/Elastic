package net.merayen.elastic.netlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

public final class NetList {
	@SuppressWarnings("serial") public static class NetListException extends RuntimeException {}
	@SuppressWarnings("serial") public static class NodeNotFound extends NetListException {}
	@SuppressWarnings("serial") public static class PortNotFound extends NetListException {}
	@SuppressWarnings("serial") public static class ConnectionNotFound extends NetListException {}
	@SuppressWarnings("serial") public static class AlreadyConnected extends NetListException {}

	final List<Node> nodes = new ArrayList<Node>();
	final List<Line> lines = new ArrayList<Line>();

	public NetList() {}

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
				if(node.id.equals(line.node_a.id))
					node_a = node;
				if(node.id.equals(line.node_b.id))
					node_b = node;
			}

			if(node_a == null || node_b == null)
				throw new RuntimeException("Should not happen");

			this.lines.add(new Line(getNode(node_a.id), line.port_a, getNode(node_b.id), line.port_b));
		}
	}

	public Node createNode() {
		return createNode(new Integer(UUID.randomUUID().hashCode()).toString());
	}

	public Node createNode(String node_id) {
		for(Node n : nodes)
			if(n.id.equals(node_id))
				throw new RuntimeException("Node with ID '" + node_id + "' already exists");

		Node n = new Node(node_id);
		nodes.add(n);
		return n;
	}

	/**
	 * Copies and inserts the node. Node can be from another NetList. Lines are
	 * not copied. Ports and their properties are also copied, but watch out:
	 * The properties are not deeply copied! Returns the new node.
	 */
	public Node adaptNode(Node node) {
		for(Node n : nodes)
			if(n.id.equals(node.id))
				throw new RuntimeException("Could not adapt node: Node with same id already eixts");

		Node new_node = node.copy();

		nodes.add(new_node);

		return new_node;
	}

	public void remove(String node_id) {
		remove(getNode(node_id));
	}

	public void remove(Node node) {
		if(!nodes.contains(node))
			throw new NodeNotFound();

		// Disconnect all lines for this node
		for(String p : node.ports.keySet())
			disconnectAll(node, p);

		while(nodes.remove(node)); // Should only be one
	}

	public List<Node> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public List<Line> getLines() {
		return new ArrayList<>(lines);
	}

	public Port createPort(String node, String port) {
		return createPort(getNode(node), port);
	}

	public Port createPort(Node node, String port) {
		Port p = new Port();
		node.ports.put(port, p);
		return p;
	}

	public void removePort(String node, String port) {
		removePort(getNode(node), port);
	}

	public void removePort(Node node, String port) {
		for(int i = lines.size(); i > -1; i--) { // Remove all connections to port
			Line line = lines.get(i);
			if((line.node_a == node && line.port_a.equals(port)) || (line.node_b == node && line.port_b.equals(port)))
				lines.remove(i);
		}

		node.ports.remove(port);
	}

	public Port getPort(String node, String port) {
		return getPort(getNode(node), port);
	}

	public Port getPort(Node node, String port) {
		return node.ports.get(port);
	}

	public String[] getPorts(Node node) {
		return node.ports.keySet().toArray(new String[0]);
	}

	public Node getNode(String id) {
		for(Node node : nodes)
			if(node.id.equals(id))
				return node;

		throw new RuntimeException(String.format("Node by id %s was not found", id));
	}

	public boolean hasNode(String id) {
		for(Node node : nodes)
			if(node.id.equals(id))
				return true;

		return false;
	}

	public void connect(String node_a, String port_a, String node_b, String port_b) {
		connect(getNode(node_a), port_a, getNode(node_b), port_b);
	}

	public void connect(Node node_a, String port_a, Node node_b, String port_b) {
		if(!nodes.contains(node_a) || !nodes.contains(node_b))
			throw new RuntimeException("Node(s) does not belong to this NetList");

		if(node_a == node_b)
			throw new RuntimeException("Node can not connect to itself");

		if(!node_a.ports.containsKey(port_a) || !node_b.ports.containsKey(port_b))
			throw new PortNotFound();

		if(hasConnection(node_a, port_a, node_b, port_b))
			throw new AlreadyConnected();

		Line line = new Line(node_a, port_a, node_b, port_b);
		lines.add(line);
	}

	public void disconnect(String node_a, String port_a, String node_b, String port_b) {
		disconnect(getNode(node_a), port_a, getNode(node_b), port_b);
	}

	public void disconnect(Node node_a, String port_a, Node node_b, String port_b) {
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

	public void disconnectAll(Node node, String port) {
		for(Line l : getConnections(node, port))
			lines.remove(l);
	}

	public List<Line> getConnections(String node, String port) {
		return getConnections(getNode(node), port);
	}

	public List<Line> getConnections(Node node, String port) {
		List<Line> result = new ArrayList<>();

		for(Line l : lines)
			if(l.node_a == node && l.port_a.equals(port))
				result.add(l);
			else if(l.node_b == node && l.port_b.equals(port))
				result.add(l);

		return result;
	}

	public String getPortName(Node node, Port port) {
		for(Entry<String, Port> x : node.ports.entrySet())
			if(x.getValue() == port)
				return x.getKey();

		return null;
	}

	public boolean isConnected(String node_a, String port_a, String node_b, String port_b) {
		return isConnected(getNode(node_a), port_a, getNode(node_b), port_b);
	}

	public boolean isConnected(Node node_a, String port_a, Node node_b, String port_b) {
		for(Line l : lines)
			if(l.node_a == node_a && l.node_b == node_b && l.port_a.equals(port_a) && l.port_b.equals(port_b))
				return true;
			else if(l.node_b == node_a && l.node_a == node_b && l.port_b.equals(port_a) && l.port_a.equals(port_b))
				return true;

		return false;
	}

	public NetList copy() {
		return new NetList(nodes, lines);
	}

	public void clear() {
		lines.clear();
		nodes.clear();
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

	private boolean hasConnection(Node node_a, String port_a, Node node_b, String port_b) {
		for(Line l : getConnections(node_a, port_a))
			if((l.node_a == node_a && l.port_a.equals(port_a)) && (l.node_b == node_b && l.port_b.equals(port_b)))
				return true;
			else if((l.node_a == node_b && l.port_a.equals(port_b)) && (l.node_b == node_a && l.port_b.equals(port_a)))
				return true;

		return false;
	}
}
