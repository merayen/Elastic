package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * Midi data sent outside the score.
 *
 * This could be for example the user pushing down a tangent on the keyboard.
 *
 * Does not contain any timing data as of now. We might add it if keys tend to "stick together" when user plays.
 */
class DirectMidiMessage(override val nodeId: String, val midi: ShortArray) : NodeDataMessage {
	init {
		if (midi.size % 3 != 0)
			throw RuntimeException("midi data array must have 3 byte package lengths")

		if (midi.isEmpty())
			error("Direct midi data must have data")
	}
}