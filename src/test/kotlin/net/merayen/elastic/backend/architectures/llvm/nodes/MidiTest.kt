package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.midi_1.MidiDataMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.backend.midi.MidiStatuses
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MidiTest : LLVMNodeTest() {
	/**
	 * Test sending in direct midi data (e.g midi from user playing on keyboard)
	 */
	@Test
	fun `output direct midi`() {
		val supervisor = create()

		// Create the nodes and netlist
		supervisor.ingoing.send(CreateNodeMessage("midi", "midi", "top"))
		supervisor.ingoing.send(CreateNodeMessage("midi_out", "midi_out", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("midi", "out", Format.MIDI))
		supervisor.ingoing.send(CreateNodePortMessage("midi_out", "in"))
		supervisor.ingoing.send(NodeConnectMessage("midi", "out", "midi_out", "in"))

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

		val midiNodeProperties = Properties(eventZones)
		supervisor.ingoing.send(NodePropertyMessage("midi", midiNodeProperties))
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()
	}
}