package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public class Util {
	private final NetList netlist;

	public Util(NetList netlist) {
		this.netlist = netlist;
	}

	public boolean isOutput(Node node, String port) {
		Port p = netlist.getPort(node, port);
		if(p == null)
			throw new RuntimeException("Port " + port + " does not exist");

		return (boolean)p.properties.get("output");
	}

	public boolean isOutput(Port port) {
		return (boolean)port.properties.get("output");
	}

	public List<String> getInputPorts(Node node) {
		List<String> result = new ArrayList<>();

		for(String port : netlist.getPorts(node))
			if(!isOutput(netlist.getPort(node, port)))
				result.add(port);

		return result;
	}

	public List<String> getOutputPorts(Node node) {
		List<String> result = new ArrayList<>();

		for(String port : netlist.getPorts(node))
			if(isOutput(netlist.getPort(node, port)))
				result.add(port);

		return result;
	}

	public int getPolyNo(Node node, String port) {
		Port p = netlist.getPort(node, port);
		return (int)p.properties.get("poly_no");
	}
}
