package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.value_1.Properties
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GroupTest : LLVMNodeTest() {
	@Test
	fun `forward signal out nodes and sum them`() {
		val supervisor = createSupervisor(true)
		for (i in 0 until 2) { // Create two similar networks, we expect the out_value_X value to be 1 + 2 = 3
			supervisor.ingoing.send(
				listOf(
					CreateNodeMessage("value_$i", "value", "top"),
					CreateNodePortMessage("value_$i", "out", Format.SIGNAL),
					NodePropertyMessage("value_$i", Properties(value = 1f + i)),

					CreateNodeMessage("out_$i", "out", "top"),
					CreateNodePortMessage("out_$i", "in"),

					NodeConnectMessage("value_$i", "out", "out_$i", "in"),
				)
			)
		}

		supervisor.ingoing.send(ProcessRequestMessage())

		supervisor.onUpdate()

		val result = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData

		assertEquals(2, result.outSignal.size)
		assertEquals(0, result.outAudio.size)
		assertEquals(0, result.outMidi.size)

		for ((outNodeIndex, signal) in result.outSignal.entries.sortedBy { it.key }.map { it.value }.withIndex()) {
			assertEquals(256, signal.size, "Expected signal output to be exact 256 samples")

			for (i in 0 until 256)
				assertEquals(outNodeIndex + 1f, signal[i], "For out_$i node, expected ${outNodeIndex + 1f} but got ${signal[i]} at position $i")
		}
	}
}