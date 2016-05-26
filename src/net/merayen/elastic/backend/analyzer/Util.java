package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public class Util {
	private final NetList netlist;

	Util(NetList netlist) {
		this.netlist = netlist;
	}

	public boolean isOutput(Node node, String port) {
		return (boolean)netlist.getPort(node, port).properties.get("output");
	}

	public boolean isOutput(Port port) {
		return (boolean)port.properties.get("output");
	}

	public List<String> getInputPorts(Node node) {
		List<String> result = new ArrayList<>();

		for(String port : netlist.getPorts(node)) {
			if(!isOutput(netlist.getPort(node, port)))
				result.add(port);
		}

		return result;
	}

	public List<String> getOutputPorts(Node node) {
		List<String> result = new ArrayList<>();

		for(String port : netlist.getPorts(node)) {
			if(isOutput(netlist.getPort(node, port)))
				result.add(port);
		}

		return result;
	}
}
