package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.analyzer.node_dependency.flattenDependencyList
import net.merayen.elastic.backend.analyzer.node_dependency.toDependencyList
import net.merayen.elastic.backend.architectures.llvm.nodes.PreProcessor
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.util.NetListMessages
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LLVMNetListTest {
	@Test
	fun `inject group node preprocessor node`() {
		val netList = NetList()
		listOf(
			CreateNodeMessage("top", "top", null),
			CreateNodeMessage("midi", "midi", "top"),
			CreateNodePortMessage("midi", "out", Format.MIDI),

			CreateNodeMessage("midi_poly", "midi_poly", "top"),
			CreateNodePortMessage("midi_poly", "in"),
			CreateNodePortMessage("midi_poly", "out", Format.SIGNAL),

			NodeConnectMessage("midi", "out", "midi_poly", "in"),

			CreateNodeMessage("value", "value", "midi_poly"),
			CreateNodePortMessage("value", "out", Format.SIGNAL),

			CreateNodeMessage("out", "out", "midi_poly"),
			CreateNodePortMessage("out", "in"),

			NodeConnectMessage("value", "out", "out", "in"),

			CreateNodeMessage("output", "out", "top"),
			CreateNodePortMessage("output", "in"),

			NodeConnectMessage("midi_poly", "out", "output", "in"),
		).forEach { NetListMessages.apply(netList, it) }

		assertEquals(6, netList.nodes.size, "Something else is wrong with NetList or NetListMessages...?")

		val result = LLVMNetList.process(netList)

		val nodeProperties = NodeProperties(result)

		assertEquals(
			listOf("top", "midi", "midi_poly", "value", "_preprocessor", "out", "out").sorted(),
			result.nodes.map { nodeProperties.getName(it) }.sorted(),
			"Expected to find all the nodes we added, plus the _preprocessor node that should have been added automatically"
		)

		// Find the preprocessor
		val preprocessorId = result.nodes.first { nodeProperties.getName(it) == getName(PreProcessor::class) }.id

		val dependencyList = toDependencyList(result)
		flattenDependencyList(dependencyList, result)

		assertEquals(
			mapOf(
				"top" to setOf("midi", preprocessorId, "midi_poly", "output"),
				"midi" to setOf(),
				preprocessorId to setOf("midi"),
				"midi_poly" to setOf("midi", preprocessorId, "value", "out"),
				"value" to setOf("midi", preprocessorId),
				"out" to setOf("midi", preprocessorId, "value"),
				"output" to setOf("midi_poly"),
			),
			dependencyList
		)

		println(dependencyList)
	}
}