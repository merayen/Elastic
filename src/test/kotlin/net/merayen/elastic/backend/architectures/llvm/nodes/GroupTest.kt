package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.midi_1.DirectMidiMessage
import net.merayen.elastic.backend.logicnodes.list.value_1.Properties
import net.merayen.elastic.backend.midi.MidiMessagesCreator
import net.merayen.elastic.backend.midi.MidiStatuses
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
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
				assertEquals(
					outNodeIndex + 1f,
					signal[i],
					"For out_$i node, expected ${outNodeIndex + 1f} but got ${signal[i]} at position $i"
				)
		}
	}

	@Test
	@Disabled
	fun `forward audio out nodes and sum them`() {
		val supervisor = createSupervisor(true)
		supervisor.ingoing.send(
			listOf(
				TODO()
			)
		)
	}

	@Test
	fun `forward midi and signal out nodes`() {
		val supervisor = createSupervisor(true)
		supervisor.ingoing.send(
			listOf(
				// Start by creating a midi node
				CreateNodeMessage("midi", "midi", "top"),
				CreateNodePortMessage("midi", "out", Format.MIDI),
				// Send a key down midi event that should be sent out again via the midi out node below, id="out_midi"
				DirectMidiMessage("midi", midi = MidiMessagesCreator.keyDown(10, 1f)),

				CreateNodeMessage("out_midi", "out", "top"),
				CreateNodePortMessage("out_midi", "in"),

				// Then connect the midi and out-node together
				NodeConnectMessage("midi", "out", "out_midi", "in"),

				// Also create a signal value, this will also test the midi offset in the data stream from C backend to Java
				CreateNodeMessage("value", "value", "top"),
				CreateNodePortMessage("value", "out", Format.SIGNAL),
				NodePropertyMessage("value", Properties(value = 1f)),

				CreateNodeMessage("out_signal", "out", "top"),
				CreateNodePortMessage("out_signal", "in"),

				NodeConnectMessage("value", "out", "out_signal", "in"),

				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		val result = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData

		assertEquals(1, result.outMidi.size, "top node should have forwarded midi from the out_midi node")

		assertTrue("out_midi" in result.outMidi)

		assertEquals(MidiMessagesCreator.keyDown(10, 1f).toList(), result.outMidi["out_midi"]!!.toList())
	}
}