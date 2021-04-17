package net.merayen.elastic.backend.analyzer.node_dependency

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.util.NetListMessages
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class NetListToNodeListKtTest {
	@Test
	fun `simple NetList`() {
		val netlist = NetList()
		NetListMessages.apply(netlist, CreateNodeMessage("a", "a", 1, null))
		NetListMessages.apply(netlist, CreateNodeMessage("b", "b", 1, null))

		val nodelist = toDependencyList(netlist)
		assertEquals(setOf("a", "b"), nodelist.keys)

		flattenDependencyList(nodelist, netlist) // Should make no difference
		assertEquals(setOf("a", "b"), nodelist.keys)
	}

	@Test
	fun `cyclic dependency`() {
		val netlist = NetList()
		NetListMessages.apply(netlist, CreateNodeMessage("a", "a", 1, null))
		NetListMessages.apply(netlist, CreateNodeMessage("b", "b", 1, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("a", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("a", "in", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("b", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("b", "in", false, null))
		NetListMessages.apply(netlist, NodeConnectMessage("a", "out", "b", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("b", "out", "a", "in"))

		val nodelist = toDependencyList(netlist)
		assertEquals(mapOf("a" to setOf("b"), "b" to setOf("a")), nodelist)

		flattenDependencyList(nodelist, netlist) // Should make no difference
		assertEquals(mapOf("a" to setOf("b"), "b" to setOf("a")), nodelist)
	}

	@Test
	fun nested() {
		val netlist = createNetList()

		val nodelist = toDependencyList(netlist)

		assertEquals(setOf("A", "a"), nodelist.getSources())
		assertEquals(setOf("D", "d"), nodelist.getTargets())

		assertEquals(setOf("a", "b", "c", "d", "A", "B", "C", "D"), nodelist.keys)

		flattenDependencyList(nodelist, netlist)

		// When we flattened the NodeList, all children nodes are now depending on each other
		assertEquals(setOf("a"), nodelist.getSources())
		assertEquals(setOf("d"), nodelist.getTargets())

		assertEquals(setOf("a", "b", "c", "d", "A", "B", "C", "D"), nodelist.keys)
	}

	@Test
	fun `all group nodes depends on their children`() {
		val netlist = NetList()
		listOf(
			CreateNodeMessage("top", "top", null),
			CreateNodeMessage("a", "a", "top"),
			CreateNodePortMessage("a", "out", Format.SIGNAL),

			CreateNodeMessage("b", "b", "top"),
			CreateNodePortMessage("b", "in"),
			CreateNodePortMessage("b", "out", Format.SIGNAL),

			NodeConnectMessage("a", "out", "b", "in"),

			CreateNodeMessage("c", "c", "b"),
			CreateNodePortMessage("c", "out", Format.SIGNAL),

			CreateNodeMessage("d", "d", "b"),
			CreateNodePortMessage("d", "in"),

			NodeConnectMessage("c", "out", "d", "in"),

			CreateNodeMessage("e", "e", "top"),
			CreateNodePortMessage("e", "in"),

			NodeConnectMessage("b", "out", "e", "in"),
		).forEach { NetListMessages.apply(netlist, it) }

		val nodelist = toDependencyList(netlist)
		flattenDependencyList(nodelist, netlist)

		assertEquals(
			mapOf(
				"top" to setOf("a", "b", "e"),
				"a" to setOf(),
				"b" to setOf("a", "c", "d"),
				"c" to setOf("a"),
				"d" to setOf("a", "c"),
				"e" to setOf("b"),
			),
			nodelist
		)
	}

	@Test
	@Disabled
	fun `dependency list`() {
		val netlist = NetList()

		NetListMessages.apply(netlist, CreateNodeMessage("top", "top", 1, null))
		NetListMessages.apply(netlist, CreateNodeMessage("value1", "value", 1, "top"))
		NetListMessages.apply(netlist, CreateNodeMessage("group", "group", 1, "top"))
		NetListMessages.apply(netlist, CreateNodeMessage("in", "in", 1, "group"))
		NetListMessages.apply(netlist, CreateNodeMessage("value2", "value", 1, "group"))
		NetListMessages.apply(netlist, CreateNodeMessage("add", "add", 1, "group"))
		NetListMessages.apply(netlist, CreateNodeMessage("out", "out", 1, "group"))
		NetListMessages.apply(netlist, CreateNodeMessage("output", "out", 1, "top"))

		// Not connected nodes. Should be executed at the beginning
		NetListMessages.apply(netlist, CreateNodeMessage("group2", "group", 1, "top"))
		NetListMessages.apply(netlist, CreateNodeMessage("add2", "add", 1, "group2"))

		NetListMessages.apply(netlist, CreateNodePortMessage("value1", "out", Format.SIGNAL))
		NetListMessages.apply(netlist, CreateNodePortMessage("group", "in"))
		NetListMessages.apply(netlist, CreateNodePortMessage("in", "out", Format.SIGNAL))
		NetListMessages.apply(netlist, CreateNodePortMessage("value2", "out", Format.SIGNAL))
		NetListMessages.apply(netlist, CreateNodePortMessage("add", "in1"))
		NetListMessages.apply(netlist, CreateNodePortMessage("add", "in2"))
		NetListMessages.apply(netlist, CreateNodePortMessage("add", "out", Format.SIGNAL))
		NetListMessages.apply(netlist, CreateNodePortMessage("out", "in"))
		NetListMessages.apply(netlist, CreateNodePortMessage("group", "out", Format.SIGNAL))
		NetListMessages.apply(netlist, CreateNodePortMessage("output", "in"))

		NetListMessages.apply(netlist, NodeConnectMessage("value1", "out", "group", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("value2", "out", "add", "in1"))
		NetListMessages.apply(netlist, NodeConnectMessage("in", "out", "add", "in2"))
		NetListMessages.apply(netlist, NodeConnectMessage("add", "out", "out", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("group", "out", "output", "in"))

		TODO()
	}

	private fun createNetList(): NetList {
		val netlist = NetList()
		NetListMessages.apply(netlist, CreateNodeMessage("a", "a", 1, null)) // Source node
		NetListMessages.apply(netlist, CreateNodeMessage("b", "a", 1, null)) // In parallel with c node
		NetListMessages.apply(netlist, CreateNodeMessage("c", "a", 1, null)) // group-node having children
		NetListMessages.apply(netlist, CreateNodeMessage("d", "a", 1, null)) // Output node

		NetListMessages.apply(netlist, CreateNodeMessage("A", "a", 1, "c")) // child 1 of group-node
		NetListMessages.apply(netlist, CreateNodeMessage("B", "a", 1, "c")) // child 1 of group-node

		NetListMessages.apply(netlist, CreateNodeMessage("C", "a", 1, "c")) // child 1 of group-node, cyclic
		NetListMessages.apply(netlist, CreateNodeMessage("D", "a", 1, "c")) // child 1 of group-node, cyclic

		// Create ports
		NetListMessages.apply(netlist, CreateNodePortMessage("a", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("b", "in", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("b", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("c", "in", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("c", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("d", "in1", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("d", "in2", false, null))

		NetListMessages.apply(netlist, CreateNodePortMessage("A", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("B", "in", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("B", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("C", "in", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("C", "out", true, Format.AUDIO))
		NetListMessages.apply(netlist, CreateNodePortMessage("D", "in1", false, null))
		NetListMessages.apply(netlist, CreateNodePortMessage("D", "in2", false, null))

		// Connect things
		NetListMessages.apply(netlist, NodeConnectMessage("a", "out", "b", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("a", "out", "c", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("b", "out", "d", "in1"))
		NetListMessages.apply(netlist, NodeConnectMessage("c", "out", "d", "in2"))

		NetListMessages.apply(netlist, NodeConnectMessage("A", "out", "D", "in1"))
		NetListMessages.apply(netlist, NodeConnectMessage("B", "out", "C", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("C", "out", "B", "in"))
		NetListMessages.apply(netlist, NodeConnectMessage("C", "out", "D", "in2"))

		return netlist
	}
}