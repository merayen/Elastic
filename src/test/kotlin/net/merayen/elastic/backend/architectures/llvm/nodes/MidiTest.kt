package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.midi_1.DirectMidiMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeMidiOut
import net.merayen.elastic.backend.midi.MidiMessagesCreator
import net.merayen.elastic.backend.midi.MidiStatuses
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class MidiTest : LLVMNodeTest() {
	/**
	 * Test sending in direct midi data (e.g midi from user playing on keyboard)
	 */
	@Test
	fun `output direct midi`() {
		val supervisor = createSupervisor()

		val midiMessage = MidiMessagesCreator.keyDown(10, 1f)

		// Create the nodes and netlist
		supervisor.ingoing.send(CreateNodeMessage("midi", "midi", "top"))
		supervisor.ingoing.send(CreateNodeMessage("out", "out", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("midi", "out", Format.MIDI))
		supervisor.ingoing.send(CreateNodePortMessage("out", "in"))
		supervisor.ingoing.send(NodeConnectMessage("midi", "out", "out", "in"))

		supervisor.ingoing.send(DirectMidiMessage("midi", midiMessage))
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()

		val result = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
		assertEquals(midiMessage.toList(), result.outMidi["out"]!!.toList())
	}

	@Test
	@Disabled
	fun `output score`() {
		// Create a midi score
		val eventZones = Properties.EventZones()
		eventZones.add(
			Properties.EventZone(
				"1",
				0.0f,
				1.0f,
				MidiData(
					mutableListOf(
						MidiData.MidiMessage(
							"1",
							0.0,
							mutableListOf(MidiStatuses.KEY_DOWN, 32, 64)
						),
						MidiData.MidiMessage(
							"2",
							0.1,
							mutableListOf(MidiStatuses.KEY_UP, 32, 64)
						)
					)
				)
			)
		)
		TODO()
	}
}