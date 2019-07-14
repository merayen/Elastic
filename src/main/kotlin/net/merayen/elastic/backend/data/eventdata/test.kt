package net.merayen.elastic.backend.data.eventdata

import net.merayen.elastic.backend.midi.MidiMessagesCreator
import net.merayen.elastic.util.UniqueID

fun test() {
	testMidiData()
}

fun testMidiData() {
	val midiData = MidiData()
	midiData.add(
			MidiData.MidiChunk(
					UniqueID.create(),
					0.0,
					arrayOf(MidiMessagesCreator.keyDown(10, 1f))
			)
	)

	midiData.add(MidiData.MidiChunk(
			UniqueID.create(),
			0.0,
			arrayOf(MidiMessagesCreator.keyDown(11, 0.8f))
	))

	midiData.add(MidiData.MidiChunk( // Overlaps tangent 10, so this should be discarded
			UniqueID.create(),
			0.1,
			arrayOf(MidiMessagesCreator.keyDown(10, 0.5f))
	))

	midiData.add(MidiData.MidiChunk( // Releases a tangent that isn't pressed. Should be discarded
			UniqueID.create(),
			0.1,
			arrayOf(MidiMessagesCreator.keyDown(12, 0.5f))
	))
}