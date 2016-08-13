package net.merayen.elastic.backend.analyzer;

import java.util.List;

import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

/**
 * Warning: Changing the netlist while walking can have bad results.
 */
public class Walker {
	@SuppressWarnings("serial")
	public static class WalkException extends RuntimeException {
		WalkException(String msg) {
			super(msg);
		}
	}

	private final NodeProperties nodeProperties;
	private final NetList netlist;
	private Node current;

	public Walker(NetList netlist, Node node) {
		this.netlist = netlist;
		this.current = node;
		this.nodeProperties = new NodeProperties(netlist);
	}

	/**
	 * Walk to the left, through a port. 
	 */
	public void walkLeft(String port) {
		if(nodeProperties.isOutput(current, port))
			throw new WalkException("Can't walk left: Port is not input");

		List<Line> lines = netlist.getConnections(current, port);

		if(lines.size() == 0)
			throw new WalkException("Can not walk by this line, as it is not connected");
		else if(lines.size() != 1)
			throw new WalkException("Should not happen");

		Line line = lines.get(0);
		
		if(line.node_a == current)
			current = line.node_b;
		else if(line.node_b == current)
			current = line.node_a;
		else
			throw new RuntimeException("Should not happen");
	}

	public void walkRight(Line line) {
		String our_port, dest_port;
		Node dest_node;

		if(line.node_a == current) {
			our_port = line.port_a;
			dest_port = line.port_b;
			dest_node = line.node_b;
		} else if(line.node_b == current) {
			our_port = line.port_b;
			dest_port = line.port_a;
			dest_node = line.node_a;
		} else {
			throw new WalkException("Can not walk by this line, as it is not connected to current node");
		}

		if(!nodeProperties.isOutput(current, our_port))
			throw new WalkException("Can't walk right: Port is not output");

		if(netlist.getPort(dest_node, dest_port) == null)
			throw new WalkException("Should not happen");

		if(nodeProperties.isOutput(dest_node, dest_port))
			throw new RuntimeException("Should not happen");

		current = dest_node;
	}

	public void jumpTo(Node node) {
		if(netlist.getNode(node.getID()) != node)
			throw new RuntimeException("Should not happen");

		current = node;
	}

	public Node getCurrent() {
		return current;
	}

	public List<String> getInputs() {
		return nodeProperties.getInputPorts(current);
	}

	public List<String> getOutputs() {
		return nodeProperties.getOutputPorts(current);
	}

	public Line getInputConnection(String port) {
		if(nodeProperties.isOutput(current, port))
			throw new RuntimeException("Port must be an input-port");

		List<Line> line = netlist.getConnections(current, port);

		return line.size() == 1 ? line.get(0) : null;
	}

	public List<Line> getOutputConnections(String port) {
		if(!nodeProperties.isOutput(current, port))
			throw new RuntimeException("Port must be an output-port");

		return netlist.getConnections(current, port);
	}
}
