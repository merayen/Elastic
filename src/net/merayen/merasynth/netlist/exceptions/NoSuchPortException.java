package net.merayen.merasynth.netlist.exceptions;

import net.merayen.merasynth.netlist.Node;

public class NoSuchPortException extends RuntimeException {
	final Node node;
	final String port_name;

	public NoSuchPortException(Node node, String port_name) {
		this.node = node;
		this.port_name = port_name;
	}

	public String toString() {
		return String.format("Port %s was not found on %s", port_name, node);
	}
}
