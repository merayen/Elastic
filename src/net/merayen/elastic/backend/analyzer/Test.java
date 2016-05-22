package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public class Test {
	private Test() {}

	public static void test() {
		NetList netlist = new NetList();
		Node midi_in = createNode(netlist, "midi_in", 1);
		addPort(netlist, midi_in, "output", true, 0);

		Node poly = createNode(netlist, "poly", 1);
		addPort(netlist, poly, "input", false, 0);
		addPort(netlist, poly, "output", true, 1);

		Node adsr = createNode(netlist, "adsr", 1);
		addPort(netlist, adsr, "input", true, 0);
		addPort(netlist, adsr, "output", true, 0);
		addPort(netlist, adsr, "fac", true, 0);

		Node sgen = createNode(netlist, "sgen", 1);
		addPort(netlist, sgen, "frequency", false, 0);
		addPort(netlist, sgen, "output", true, 0);

		//Node mix = createNode(netlist, "mix", 1);

		Node depoly = createNode(netlist, "depoly", 1);
		addPort(netlist, depoly, "input", true, -1);

		Analyzer.analyze(netlist);
	}

	private static Node createNode(NetList netlist, String name, Integer version) {
		Node n = netlist.createNode();
		n.properties.put("name", name);
		n.properties.put("version", version);
		return n;
	}

	private static Port addPort(NetList netlist, Node node, String port, boolean output, int poly_no) {
		Port p = netlist.createPort(node, port);
		p.properties.put("output", output);
		p.properties.put("poly_no", poly_no);
		return p;
	}
}
