package net.merayen.elastic.backend.analyzer

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.netlist.Port
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class AnalyzerTest {
	private lateinit var netlist: NetList

	@BeforeEach
	fun setUp() {
		netlist = NetList()
		populateNetList()
	}

	@Test
	fun testAnalyzer() {
		val properties = NodeProperties(netlist)
		Analyzer.analyze(netlist)
	}

	@Test
	fun testWalker() {
		val w = Walker(netlist, netlist.getNode("midi_in"))

		assertEquals(0, w.inputs.size)

		assertEquals(1, w.outputs.size)
		assertTrue(w.outputs.contains("output"))

		assertEquals(w.current, netlist.getNode("midi_in"))

		val lines = w.getOutputConnections("output")
		assertEquals(1, lines.size)

		for (l in lines) { // Walks to poly
			if (l.node_a == netlist.getNode("poly")) {
				w.walkRight(l)
			} else if (l.node_b == netlist.getNode("poly")) {
				w.walkRight(l)
			}
		}

		// Testing with poly
		assertEquals(w.current, netlist.getNode("poly"))

		assertEquals(1, w.inputs.size)
		assertTrue(w.inputs.contains("input"))

		//if(w.getInputConnection("frequency") != netlist.getConnections(w.getCurrent(), "frequency").get(0))
		//	no();

		//if(w.getOutputConnections("output").size() != 1 || w.getOutputConnections("output").get(0) != netlist.getConnections(w.getCurrent(), "output").get(0))
		//	no();

		// Walk all the way to output-node
		while (w.current != netlist.getNode("output"))
			w.walkRight(w.getOutputConnections("output")[0])

		// We are on the rightmost output-node. Walk left twice
		w.walkLeft("input")
		w.walkLeft("input")

		assertEquals(w.current, netlist.getNode("sgen"))

		// On sgen currently, walk to the adsr
		w.walkLeft("frequency")

		assertEquals(w.current, netlist.getNode("adsr"))

		// Jump directly to sgen_direct
		w.jumpTo(netlist.getNode("adsr"))

		assertEquals(w.current, netlist.getNode("adsr"))
	}

	@Test
	fun testTraverser() {
		val t = Traverser(netlist)

		var nodes = t.getLeftMost(netlist.getNode("output"))
		assertEquals(2, nodes.size)
		assertTrue(nodes.contains(netlist.getNode("midi_in")))
		assertTrue(nodes.contains(netlist.getNode("adsr_tail3")))

		nodes = t.getRightMost(netlist.getNode("midi_in"))
		assertEquals(1, nodes.size)
		assertTrue(nodes.contains(netlist.getNode("output")))

		nodes = t.getAllInGroup(netlist.getNode("adsr"))

		val expected = arrayOf(netlist.getNode("midi_in"), netlist.getNode("poly"), netlist.getNode("adsr"), netlist.getNode("sgen"), netlist.getNode("depoly"), netlist.getNode("output"),
			//netlist.getNode("sgen_direct"),
			netlist.getNode("poly_feedback"), netlist.getNode("adsr_tail"), netlist.getNode("adsr_tail2"), netlist.getNode("adsr_tail3"))

		assertEquals(nodes.size, expected.size)

		assertTrue(expected.all { nodes.contains(it) })

		nodes = t.getAllInGroup(netlist.getNode("test_a")) // Loop test, should not hang

		assertEquals(2, nodes.size)

		assertTrue(nodes.contains(netlist.getNode("test_a")))
		assertTrue(nodes.contains(netlist.getNode("test_b")))

		// Test creation of copying NetList() with certain Node()s only
		val nodes_to_copy = ArrayList<Node>()
		nodes_to_copy.add(netlist.getNode("midi_in"))
		nodes_to_copy.add(netlist.getNode("poly"))
		nodes_to_copy.add(netlist.getNode("adsr"))
		nodes_to_copy.add(netlist.getNode("sgen"))
		// nodes_to_copy.add(netlist.getNode("depoly")); // Leaving this one out on purpose, should now create a NetList with 2 groups
		nodes_to_copy.add(netlist.getNode("output"))
		//nodes_to_copy.add(netlist.getNode("sgen_direct"));
		nodes_to_copy.add(netlist.getNode("poly_feedback"))
		nodes_to_copy.add(netlist.getNode("adsr_tail"))
		nodes_to_copy.add(netlist.getNode("adsr_tail2"))
		nodes_to_copy.add(netlist.getNode("adsr_tail3"))

		val new_netlist = t.copyNetList(netlist, nodes_to_copy)

		assertEquals(new_netlist.nodes.size, nodes_to_copy.size)

		assertTrue(nodes_to_copy.all { new_netlist.hasNode(it.id) })

		// Check that all connections are valid. output and sgen are no more connected to depoly as it has been removed
		nodes_to_copy.remove(netlist.getNode("output"))
		nodes_to_copy.remove(netlist.getNode("sgen"))

		assertTrue {
			nodes_to_copy.all {
				val node = netlist.getNode(it.id)
				netlist.getPorts(node).all {
					netlist.getConnections(node.id, it).all {
						new_netlist.isConnected(it.node_a.id, it.port_a, it.node_b.id, it.port_b)
					}
				}
			}
		}

		// We should now also have two groups, as depoly is not in the
		// nodes_to_copy, and should have left a hole between output and the
		// other nodes
		val groups = Traverser(new_netlist).groups

		assertEquals(2, groups.size)
	}

	@Test
	fun testUtil() {
		val nodeProperties = NodeProperties(netlist)

		var ports = nodeProperties.getInputPorts(netlist.getNode("sgen"))
		assertEquals(2, ports.size)
		assertTrue(ports.contains("frequency"))
		assertTrue(ports.contains("amplitude"))

		ports = nodeProperties.getOutputPorts(netlist.getNode("adsr"))
		assertEquals(2, ports.size)
		assertTrue(ports.contains("output"))
		assertTrue(ports.contains("fac"))
	}

	private fun createNode(name: String): Node {
		val np = NodeProperties(netlist)
		val n = netlist.createNode(name)
		np.setName(n, name)
		np.setVersion(n, 1)
		return n
	}

	private fun addPort(node: Node, port: String, output: Boolean, chain_ident: String?): Port {
		val np = NodeProperties(netlist)
		val p = netlist.createPort(node, port)
		if (output) {
			np.setOutput(p)
			np.setFormat(p, Format.AUDIO)
		}

		return p
	}

	private fun populateNetList() {
		// Group 1
		val midi_in = createNode("midi_in")
		addPort(midi_in, "output", true, null) // chain 0

		val poly = createNode("poly")
		addPort(poly, "input", false, null) // chain 0
		addPort(poly, "output", true, "adsf") // chain 1

		val adsr = createNode("adsr")
		addPort(adsr, "input", false, null) // chain 1
		addPort(adsr, "attack", false, null) // chain 2,3
		addPort(adsr, "decay", false, null) // chain 2,3
		addPort(adsr, "output", true, null) // chain 2,3
		addPort(adsr, "fac", true, null)

		val sgen = createNode( "sgen")
		addPort(sgen, "frequency", false, null) // chain 2,3
		addPort(sgen, "amplitude", false, null)
		addPort(sgen, "output", true, null)

		/*Node sgen_direct = createNode(netlist, "sgen_direct");
		addPort(netlist, sgen_direct, "frequency", false, null);
		addPort(netlist, sgen_direct, "amplitude", false, null);
		addPort(netlist, sgen_direct, "output", true, null);*/

		//Node mix = createNode(netlist, "mix", 1);

		val depoly = createNode("depoly")
		addPort(depoly, "input", false, "chain_consumer") // chain 2,3
		addPort(depoly, "output", true, null) // chain 0

		val output = createNode("output")
		addPort(output, "input", false, null) // chain 0

		val poly_feedback = createNode("poly_feedback") // Node used to feedback a chain into another chain
		addPort(poly_feedback, "input", false, null)
		addPort(poly_feedback, "output", true, "adsf")

		// Tail for the adsr's decay-port
		val adsr_tail = createNode("adsr_tail")
		addPort(adsr_tail, "input", false, null)
		addPort(adsr_tail, "output", true, null)

		val adsr_tail2 = createNode("adsr_tail2") // Spawns voices
		addPort(adsr_tail2, "input", false, null)
		addPort(adsr_tail2, "output", true, "asdf")

		val adsr_tail3 = createNode("adsr_tail3")
		addPort(adsr_tail3, "output", true, null)

		netlist.connect(midi_in, "output", poly, "input")
		netlist.connect(poly, "output", adsr, "input")        // Starting session 1
		netlist.connect(adsr, "output", sgen, "frequency") // Session 1
		netlist.connect(sgen, "output", depoly, "input")    // Session 1
		netlist.connect(depoly, "output", output, "input") // Ending session 1

		netlist.connect(sgen, "output", poly_feedback, "input")
		netlist.connect(poly_feedback, "output", adsr, "attack")

		netlist.connect(adsr_tail, "output", adsr, "decay")
		netlist.connect(adsr_tail2, "output", adsr_tail, "input")
		netlist.connect(adsr_tail3, "output", adsr_tail2, "input")

		/*netlist.connect(midi_in, "output", sgen_direct, "frequency");
		netlist.connect(sgen_direct, "output", sgen, "amplitude");*/

		// Group 2 - testing loops
		val test_a = createNode("test_a")
		addPort(test_a, "input", false, null) // chain 0
		addPort(test_a, "output", true, null) // chain 0

		val test_b = createNode("test_b")
		addPort(test_b, "input", false, null) // chain 0
		addPort(test_b, "output", true, null) // chain 0

		netlist.connect(test_a, "output", test_b, "input")
		netlist.connect(test_b, "output", test_a, "input")
	}
}