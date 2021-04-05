package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.midi_1.DirectMidiMessage
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
import net.merayen.elastic.backend.logicnodes.list.value_1.Properties
import net.merayen.elastic.backend.midi.MidiMessagesCreator
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MidiPolyNodeTest : LLVMNodeTest() {
	@Test
	fun `create voices`() {
		val supervisor = createSupervisor(true)

		supervisor.ingoing.send(
			listOf(
				CreateNodeMessage("midi", "midi", 1, "top"),
				CreateNodePortMessage("midi", "out", Format.MIDI),

				CreateNodeMessage("midi_poly", "midi_poly", 1, "top"),
				CreateNodePortMessage("midi_poly", "in"),
				CreateNodePortMessage("midi_poly", "out", Format.SIGNAL),

				// midi_poly children nodes
				CreateNodeMessage("value", "value", 1, "midi_poly"),
				CreateNodePortMessage("value", "out", Format.SIGNAL),
				NodePropertyMessage("value", Properties(value = 1f)),

				CreateNodeMessage("out", "out", "midi_poly"),
				CreateNodePortMessage("out", "in"),

				// Output node to read the result
				CreateNodeMessage("output", "out", "top"),
				CreateNodePortMessage("output", "in"),

				NodeConnectMessage("midi", "out", "midi_poly", "in"),
				NodeConnectMessage("value", "out", "out", "in"),
				NodeConnectMessage("midi_poly", "out", "output", "in"),
			)
		)

		for (i in 0 until 10) {
			supervisor.ingoing.send(ProcessRequestMessage())

			supervisor.onUpdate()

			val result = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertEquals((0 until 256).map { i.toFloat() }, result.outSignal["output"]!!.toList())

			// Send a key-down event which should create another voice of all the children of the midi_poly node
			supervisor.ingoing.send(
				DirectMidiMessage(
					"midi",
					MidiMessagesCreator.keyDown(i, 1f)
				)
			)
		}
	}
}