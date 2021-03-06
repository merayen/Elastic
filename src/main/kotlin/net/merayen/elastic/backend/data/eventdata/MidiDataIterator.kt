package net.merayen.elastic.backend.data.eventdata

class MidiDataIterator(private val midiData: MidiData, private var position: Double = Double.NEGATIVE_INFINITY) : Iterator<MidiData.MidiMessage> {
	private var iteratorRevision = -1L
	private var index = 0
	private var lastMidiChunkId: String? = null
	private val playedAtCurrentTime = ArrayList<MidiData.MidiMessage>() // Guard to not repeat any MidiChunk at the same position
	private var midi = midiData.getMidiChunks()

	override fun hasNext(): Boolean {
		relocate()

		return index < midi.size
	}

	override fun next(): MidiData.MidiMessage {
		relocate()

		if (index > midi.size)
			throw NoSuchElementException()

		if (position != midi[index].start)
			playedAtCurrentTime.clear()

		position = midi[index].start!!

		lastMidiChunkId = midi[index].id

		return midi[index++]
	}

	private fun relocate() {
		if (iteratorRevision == midiData.revision)
			return

		// MidiData has been changed, so we need to get a new copy of all its MidiChunks
		midi = midiData.getMidiChunks()

		// Try to relocate based on previous sent MidiChunk
		val lastMidiChunkId = lastMidiChunkId
		if (lastMidiChunkId != null) {
			val newIndex = midi.indexOfFirst { it.id == lastMidiChunkId }
			if (newIndex > -1) {
				index = newIndex + 1
				iteratorRevision = midiData.revision
				return
			}
		}

		// Couldn't relocate based on last MidiChunk, so we need to find next one based on time
		// If two or more MidiChunk shares the exact same start-time, we may drop one or more MidiChunks
		// This only happens if user changes the midi data on the same part that is getting played
		for ((newIndex, midiChunk) in midi.withIndex()) {
			if (midiChunk.start == position) {
				if (midiChunk !in playedAtCurrentTime) {
					index = newIndex
					iteratorRevision = midiData.revision
					return
				}
			} else if (midiChunk.start!! > position) {
				index = newIndex
				iteratorRevision = midiData.revision
				return
			}
		}

		// Couldn't relocate. Give up
		index = Int.MAX_VALUE
	}
}