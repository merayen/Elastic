package net.merayen.elastic.netlist;

public class Port extends NetListObject {
	Port() {}

	public Port copy() {
		Port port = new Port();
		port.properties.putAll(properties);
		return port;
	}
}
