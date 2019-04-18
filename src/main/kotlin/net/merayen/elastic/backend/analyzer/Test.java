package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.*;

import java.util.ArrayList;
import java.util.List;

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
		NodeProperties properties = new NodeProperties(netlist);
		Analyzer.analyze(netlist);

		System.out.println(Serializer.dump(netlist));
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
		if(lines.size() != 1)
			no();

		for(Line l : lines) { // Walks to poly
			if(l.node_a == netlist.getNode("poly")) {
				w.walkRight(l);
			} else if(l.node_b == netlist.getNode("poly")) {
				w.walkRight(l);
			}
		}

		// Testing with poly
		if(w.getCurrent() != netlist.getNode("poly"))
			no();

		if(w.getInputs().size() != 1 || !w.getInputs().contains("input"))
			no();

		//if(w.getInputConnection("frequency") != netlist.getConnections(w.getCurrent(), "frequency").get(0))
		//	no();

		//if(w.getOutputConnections("output").size() != 1 || w.getOutputConnections("output").get(0) != netlist.getConnections(w.getCurrent(), "output").get(0))
		//	no();

		// Walk all the way to output-node
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
		w.jumpTo(netlist.getNode("adsr"));

		if(w.getCurrent() != netlist.getNode("adsr"))
			no();
	}

	private static void testTraverser(NetList netlist) {
		Traverser t = new Traverser(netlist);

		List<Node> nodes = t.getLeftMost(netlist.getNode("output"));
		if(nodes.size() != 2 || !nodes.contains(netlist.getNode("midi_in")) || !nodes.contains(netlist.getNode("adsr_tail3")))
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
			//netlist.getNode("sgen_direct"),
			netlist.getNode("poly_feedback"),
			netlist.getNode("adsr_tail"),
			netlist.getNode("adsr_tail2"),
			netlist.getNode("adsr_tail3"),
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
		//nodes_to_copy.add(netlist.getNode("sgen_direct"));
		nodes_to_copy.add(netlist.getNode("poly_feedback"));
		nodes_to_copy.add(netlist.getNode("adsr_tail"));
		nodes_to_copy.add(netlist.getNode("adsr_tail2"));
		nodes_to_copy.add(netlist.getNode("adsr_tail3"));

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

	private static Node createNode(NetList netlist, String name) {
		NodeProperties np = new NodeProperties(netlist);
		Node n = netlist.createNode(name);
		np.setName(n, name);
		np.setVersion(n, 1);
		return n;
	}

	private static Port addPort(NetList netlist, Node node, String port, boolean output, String chain_ident) {
		NodeProperties np = new NodeProperties(netlist);
		Port p = netlist.createPort(node, port);
		if(output) {
			np.setOutput(p);
			np.setFormat(p, Format.AUDIO);
		}

		return p;
	}

	private static void populateNetList(NetList netlist) {
		// Group 1
		Node midi_in = createNode(netlist, "midi_in");
		addPort(netlist, midi_in, "output", true, null); // chain 0

		Node poly = createNode(netlist, "poly");
		addPort(netlist, poly, "input", false, null); // chain 0
		addPort(netlist, poly, "output", true, "adsf"); // chain 1

		Node adsr = createNode(netlist, "adsr");
		addPort(netlist, adsr, "input", false, null); // chain 1
		addPort(netlist, adsr, "attack", false, null); // chain 2,3
		addPort(netlist, adsr, "decay", false, null); // chain 2,3
		addPort(netlist, adsr, "output", true, null); // chain 2,3
		addPort(netlist, adsr, "fac", true, null);

		Node sgen = createNode(netlist, "sgen");
		addPort(netlist, sgen, "frequency", false, null); // chain 2,3
		addPort(netlist, sgen, "amplitude", false, null);
		addPort(netlist, sgen, "output", true, null);

		/*Node sgen_direct = createNode(netlist, "sgen_direct");
		addPort(netlist, sgen_direct, "frequency", false, null);
		addPort(netlist, sgen_direct, "amplitude", false, null);
		addPort(netlist, sgen_direct, "output", true, null);*/

		//Node mix = createNode(netlist, "mix", 1);

		Node depoly = createNode(netlist, "depoly");
		addPort(netlist, depoly, "input", false, "chain_consumer"); // chain 2,3
		addPort(netlist, depoly, "output", true, null); // chain 0

		Node output = createNode(netlist, "output");
		addPort(netlist, output, "input", false, null); // chain 0

		Node poly_feedback = createNode(netlist, "poly_feedback"); // Node used to feedback a chain into another chain
		addPort(netlist, poly_feedback, "input", false, null);
		addPort(netlist, poly_feedback, "output", true, "adsf");

		// Tail for the adsr's decay-port
		Node adsr_tail = createNode(netlist, "adsr_tail");
		addPort(netlist, adsr_tail, "input", false, null);
		addPort(netlist, adsr_tail, "output", true, null);

		Node adsr_tail2 = createNode(netlist, "adsr_tail2"); // Spawns voices
		addPort(netlist, adsr_tail2, "input", false, null);
		addPort(netlist, adsr_tail2, "output", true, "asdf");

		Node adsr_tail3 = createNode(netlist, "adsr_tail3");
		addPort(netlist, adsr_tail3, "output", true, null);

		netlist.connect(midi_in, "output", poly, "input");
		netlist.connect(poly, "output", adsr, "input");		// Starting session 1
		netlist.connect(adsr, "output", sgen, "frequency"); // Session 1
		netlist.connect(sgen, "output", depoly, "input");	// Session 1
		netlist.connect(depoly, "output", output, "input"); // Ending session 1

		netlist.connect(sgen, "output", poly_feedback, "input");
		netlist.connect(poly_feedback, "output", adsr, "attack");

		netlist.connect(adsr_tail, "output", adsr, "decay");
		netlist.connect(adsr_tail2, "output", adsr_tail, "input");
		netlist.connect(adsr_tail3, "output", adsr_tail2, "input");

		/*netlist.connect(midi_in, "output", sgen_direct, "frequency");
		netlist.connect(sgen_direct, "output", sgen, "amplitude");*/

		// Group 2 - testing loops
		Node test_a = createNode(netlist, "test_a");
		addPort(netlist, test_a, "input", false, null); // chain 0
		addPort(netlist, test_a, "output", true, null); // chain 0

		Node test_b = createNode(netlist, "test_b");
		addPort(netlist, test_b, "input", false, null); // chain 0
		addPort(netlist, test_b, "output", true, null); // chain 0

		netlist.connect(test_a, "output", test_b, "input");
		netlist.connect(test_b, "output", test_a, "input");
	}
}
