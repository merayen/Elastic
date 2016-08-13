package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.netlist.Line;
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

		populateNetList(netlist);

		testUtil(netlist);

		testTraverser(netlist);

		testWalker(netlist);

		testAnalyzer(netlist);
	}

	private static void testAnalyzer(NetList netlist) {
		AnalyzeResult ar = new Analyzer(netlist).analyze();
	}

	private static void testWalker(NetList netlist) {
		Walker w = new Walker(netlist, netlist.getNode("midi_in"));

		if(w.getInputs().size() != 0)
			no();

		if(w.getOutputs().size() != 1 || !w.getOutputs().contains("output"))
			no();

		if(w.getCurrent() != netlist.getNode("midi_in"))
			no();

		List<Line> lines = w.getOutputConnections("output");
		if(lines.size() != 2)
			no();

		for(Line l : lines) { // Walks to sgen_direct
			if(l.node_a == netlist.getNode("sgen_direct")) {
				w.walkRight(l);
			} else if(l.node_b == netlist.getNode("sgen_direct")) {
				w.walkRight(l);
			}
		}

		// Testing with sgen_direct
		if(w.getCurrent() != netlist.getNode("sgen_direct"))
			no();

		if(w.getInputs().size() != 2 || !w.getInputs().contains("frequency") || !w.getInputs().contains("amplitude"))
			no();

		if(w.getInputConnection("frequency") != netlist.getConnections(w.getCurrent(), "frequency").get(0))
			no();

		if(w.getOutputConnections("output").size() != 1 || w.getOutputConnections("output").get(0) != netlist.getConnections(w.getCurrent(), "output").get(0))
			no();

		// Walk to output-node
		while(w.getCurrent() != netlist.getNode("output"))
			w.walkRight(w.getOutputConnections("output").get(0));

		// We are on the rightmost output-node. Walk left twice
		w.walkLeft("input");
		w.walkLeft("input");

		if(w.getCurrent() != netlist.getNode("sgen"))
			no();

		// On sgen currently, walk to the adsr
		w.walkLeft("frequency");

		if(w.getCurrent() != netlist.getNode("adsr"))
			no();

		// Jump directly to sgen_direct
		w.jumpTo(netlist.getNode("sgen_direct"));

		if(w.getCurrent() != netlist.getNode("sgen_direct"))
			no();
	}

	private static void testTraverser(NetList netlist) {
		Traverser t = new Traverser(netlist);

		List<Node> nodes = t.getLeftMost(netlist.getNode("output"));
		if(nodes.size() != 1 || !nodes.contains(netlist.getNode("midi_in")))
			no();

		nodes = t.getRightMost(netlist.getNode("midi_in"));
		if(nodes.size() != 1 || !nodes.contains(netlist.getNode("output")))
			no();

		nodes = t.getAllInGroup(netlist.getNode("adsr"));

		Node[] expected = new Node[]{
			netlist.getNode("midi_in"),
			netlist.getNode("poly"),
			netlist.getNode("adsr"),
			netlist.getNode("sgen"),
			netlist.getNode("depoly"),
			netlist.getNode("output"),
			netlist.getNode("sgen_direct")
		};

		if(nodes.size() != expected.length)
			no();

		for(Node n : expected)
			if(!nodes.contains(n))
				no();

		nodes = t.getAllInGroup(netlist.getNode("test_a")); // Loop test, should not hang

		if(nodes.size() != 2)
			no();

		if(!nodes.contains(netlist.getNode("test_a")) || !nodes.contains(netlist.getNode("test_b")))
			no();

		// Test creation of copying NetList() with certain Node()s only
		List<Node> nodes_to_copy = new ArrayList<>();
		nodes_to_copy.add(netlist.getNode("midi_in"));
		nodes_to_copy.add(netlist.getNode("poly"));
		nodes_to_copy.add(netlist.getNode("adsr"));
		nodes_to_copy.add(netlist.getNode("sgen"));
		// nodes_to_copy.add(netlist.getNode("depoly")); // Leaving this one out on purpose, should now create a NetList with 2 groups
		nodes_to_copy.add(netlist.getNode("output"));
		nodes_to_copy.add(netlist.getNode("sgen_direct"));

 		NetList new_netlist = t.copyNetList(netlist, nodes_to_copy);

		if(new_netlist.getNodes().size() != nodes_to_copy.size())
			no();

		for(Node n : nodes_to_copy)
			if(!new_netlist.hasNode(n.getID()))
				no();

		// Check that all connections are valid. output and sgen are no more connected to depoly as it has been removed
		nodes_to_copy.remove(netlist.getNode("output"));
		nodes_to_copy.remove(netlist.getNode("sgen"));
		for(Node n : nodes_to_copy) {
			Node node = netlist.getNode(n.getID());
			for(String port : netlist.getPorts(node))
				for(Line l : netlist.getConnections(n.getID(), port))
					if(!new_netlist.isConnected(l.node_a.getID(), l.port_a, l.node_b.getID(), l.port_b))
						no();
		}

		// We should now also have two groups, as depoly is not in the
		// nodes_to_copy, and should have left a hole between output and the
		// other nodes
		List<NetList> groups = new Traverser(new_netlist).getGroups();

		if(groups.size() != 2)
			no();
	}

	private static void testUtil(NetList netlist) {
		NodeProperties nodeProperties = new NodeProperties(netlist);

		List<String> ports = nodeProperties.getInputPorts(netlist.getNode("sgen"));
		if(ports.size() != 2 || !ports.contains("frequency") || !ports.contains("amplitude"))
			no();

		ports = nodeProperties.getOutputPorts(netlist.getNode("adsr"));
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

	private static void populateNetList(NetList netlist) {
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

		Node sgen_direct = createNode(netlist, "sgen_direct", 1);
		addPort(netlist, sgen_direct, "frequency", false, 0);
		addPort(netlist, sgen_direct, "amplitude", false, 0);
		addPort(netlist, sgen_direct, "output", true, 0);

		//Node mix = createNode(netlist, "mix", 1);

		Node depoly = createNode(netlist, "depoly", 1);
		addPort(netlist, depoly, "input", false, -1);
		addPort(netlist, depoly, "output", true, 0);

		Node output = createNode(netlist, "output", 1);
		addPort(netlist, output, "input", false, -1);

		netlist.connect(midi_in, "output", poly, "input");
		netlist.connect(poly, "output", adsr, "input");		// Starting session 1
		netlist.connect(adsr, "output", sgen, "frequency"); // Session 1
		netlist.connect(sgen, "output", depoly, "input");	// Session 1
		netlist.connect(depoly, "output", output, "input"); // Ending session 1

		netlist.connect(midi_in, "output", sgen_direct, "frequency");
		netlist.connect(sgen_direct, "output", sgen, "amplitude");

		// Group 2 - testing loops
		Node test_a = createNode(netlist, "test_a", 1);
		addPort(netlist, test_a, "input", false, 0);
		addPort(netlist, test_a, "output", true, 0);

		Node test_b = createNode(netlist, "test_b", 1);
		addPort(netlist, test_b, "input", false, 0);
		addPort(netlist, test_b, "output", true, 0);

		netlist.connect(test_a, "output", test_b, "input");
		netlist.connect(test_b, "output", test_a, "input");
	}
}
