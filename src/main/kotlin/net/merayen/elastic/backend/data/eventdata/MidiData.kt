package net.merayen.elastic.backend.data.eventdata


// TODO split data from functions?
data class MidiData(
	var midi: MutableList<MidiMessage>? = ArrayList()
) : Cloneable, Iterable<MidiData.MidiMessage> {

	class DuplicateID(id: String) : RuntimeException(id)

	/**
	 * A chunk of midi packets.
	 * They can happen at the same time, but are ensured to be in the correct order.
	 *
	 * @param id The unique ID of the midi event
	 * @param start Offset (in beats) this MidiChunk applies
	 * @param midi Array of midi packets
	 */
	class MidiMessage(
		var id: String? = null,
		var start: Double? = null,
		var midi: MutableList<Short>? = null
	) : Cloneable {
		public override fun clone() = MidiMessage(id, start, ArrayList(midi!!))
	}

	/**
	 * Increased every time a change has been made.
	 */
	var revision = 0L

	init {
		midi!!.sortBy { it.start }
	}

	fun iterator(start: Double): MidiDataIterator {
		return MidiDataIterator(this, start)
	}

	override fun iterator(): MidiDataIterator {
		return MidiDataIterator(this)
	}

	public override fun clone(): MidiData {
		return MidiData(midi!!.map { it.clone() } as ArrayList<MidiMessage>)
	}

	fun add(midiMessage: MidiMessage) {
		if (midi!!.any { it.id == midiMessage.id })
			throw DuplicateID(midiMessage.id!!)

		midi?.add(midiMessage)
		revision++

		midi!!.sortBy { it.start }
	}

	fun addAll(midiMessages: Array<MidiMessage>) {
		for (midiChunk in midiMessages)
			if (midi!!.any { it.id == midiChunk.id })
				throw DuplicateID(midiChunk.id!!)

		midi?.addAll(midiMessages)
		revision++

		midi!!.sortBy { it.start }
	}

	fun merge(midiData: MidiData) {
		for (midiChunk in midiData.midi!!)
			if (midi!!.any { it.id == midiChunk.id })
				throw DuplicateID(midiChunk.id!!)

		for (midiPacket in midiData.midi!!)
			midi!!.add(midiPacket)

		revision++

		midi!!.sortBy { it.start }
	}

	fun remove(id: String): Boolean {
		val removed = midi!!.removeIf { it.id == id }

		revision++

		return removed
	}

	fun remove(midiMessage: MidiMessage): Boolean {
		val removed = midi!!.remove(midiMessage)

		revision++

		return removed
	}

	/* // NOT HERE! Elsewhere!
	 * Removes overlaying duplicates, zero-length notes, and then sorts.
	 */
	/*fun cleanUp() {
		midi.sortBy { it.start }

		val activeTangents = HashMap<Short, MidiChunk>()

		val result = ArrayList<MidiChunk>()

		for (midiPacket in midi) {
			val m = midiPacket.midi

			if (m[0][0] == MidiStatuses.KEY_UP) {
				if (m[0][1] !in activeTangents)
					continue // Ignore released keys that are not pushed

				activeTangents.remove(m[0][1])
			} else if (m[0][0].and(0b11110000) == MidiStatuses.KEY_DOWN) {
				if (m[0][1] in activeTangents)
					continue // Tangent already pushed

				activeTangents[m[0][1]] = midiPacket
			}

			result.add(midiPacket)
		}

		midi.clear()
		midi.addAll(result)

		revision++
	}*/

	fun getMidiChunks() = midi!!.map {
		it.clone()
	}.toTypedArray()
}
