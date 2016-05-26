package net.merayen.elastic.backend.analyzer;

import java.util.List;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public class Test {
	private Test() {}

	private static void no() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		NetList netlist = new NetList();

		// Group 1
		Node midi_in = createNode(netlist, "midi_in", 1);
		addPort(netlist, midi_in, "output", true, 0);

		Node poly = createNode(netlist, "poly", 1);
		addPort(netlist, poly, "input", false, 0);
		addPort(netlist, poly, "output", true, 1);

		Node adsr = createNode(netlist, "adsr", 1);
		addPort(netlist, adsr, "input", false, 0);
		addPort(netlist, adsr, "output", true, 0);
		addPort(netlist, adsr, "fac", true, 0);

		Node sgen = createNode(netlist, "sgen", 1);
		addPort(netlist, sgen, "frequency", false, 0);
		addPort(netlist, sgen, "amplitude", false, 0);
		addPort(netlist, sgen, "output", true, 0);

		//Node mix = createNode(netlist, "mix", 1);

		Node depoly = createNode(netlist, "depoly", 1);
		addPort(netlist, depoly, "input", false, -1);
		addPort(netlist, depoly, "output", true, 0);

		Node output = createNode(netlist, "output", 1);
		addPort(netlist, output, "input", false, -1);

		netlist.connect(midi_in, "output", poly, "input");
		netlist.connect(poly, "output", adsr, "input");
		netlist.connect(adsr, "output", sgen, "frequency");
		netlist.connect(sgen, "output", depoly, "input");
		netlist.connect(depoly, "output", output, "input");

		// Group 2 - testing loops
		Node test_a = createNode(netlist, "test_a", 1);
		addPort(netlist, test_a, "input", false, 0);

		testUtil(netlist);

		testTraverser(netlist);

		Analyzer.analyze(netlist);
	}

	private static void testTraverser(NetList netlist) {
		Traverser t = new Traverser(netlist);

		List<Node> nodes = t.getLeftMost(netlist.getNode("output"));
		if(nodes.size() != 1 || !nodes.contains(netlist.getNode("midi_in")))
			no();
	}

	private static void testUtil(NetList netlist) {
		Util util = new Util(netlist);

		List<String> ports = util.getInputPorts(netlist.getNode("sgen"));
		if(ports.size() != 2 || !ports.contains("frequency") || !ports.contains("amplitude"))
			no();

		ports = util.getOutputPorts(netlist.getNode("adsr"));
		if(ports.size() != 2 || !ports.contains("output") || !ports.contains("fac"))
			no();
	}

	private static Node createNode(NetList netlist, String name, Integer version) {
		Node n = netlist.createNode(name);
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
