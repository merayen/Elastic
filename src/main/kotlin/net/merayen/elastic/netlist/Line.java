package net.merayen.elastic.netlist;

public final class Line extends NetListObject {
	public final Node node_a;
	public final Node node_b;
	public final String port_a;
	public final String port_b;

	Line(Node node_a, String port_a, Node node_b, String port_b) {
		this.node_a = node_a;
		this.node_b = node_b;
		this.port_a = port_a;
		this.port_b = port_b;
	}
}
