package net.merayen.elastic.backend.data.eventdata

import net.merayen.elastic.backend.midi.MidiStatuses
import kotlin.experimental.and


class MidiData(
		private val midi: MutableList<MidiChunk> = ArrayList()
) : Cloneable, Iterable<MidiData.IteratorItem> {

	class IteratorItem(val midiChunk: MidiChunk)

	inner class IteratorHandler(private var position: Double) : Iterator<IteratorItem> {
		private var currentRevision = revision
		private var index = 0
		private var lastMidiChunk: MidiChunk? = null
		private var lastPosition = 0.0

		override fun hasNext(): Boolean {
			relocate()

			return index+1 < midi.size
		}

		override fun next(): IteratorItem {
			if (index >= midi.size)
				throw NoSuchElementException()

			return IteratorItem(midi[index++])
		}

		private fun relocate() {
			if (currentRevision == revision)
				return

			// Try to relocate based on previous sent MidiChunk
			val lastMidiChunk = lastMidiChunk
			if (lastMidiChunk != null) {
				val newIndex = midi.indexOf(lastMidiChunk)
				if (newIndex > -1) {
					index = newIndex
					currentRevision = revision
					return
				}
			}

			// Couldn't relocate based on last MidiChunk, so we need to find next one based on time
			// If two or more MidiChunk shares the exact same start-time, we may drop one or more MidiChunks
			// This only happens if user changes the midi data on the same part that is getting played
			var newIndex = 0
			for (midiChunk in midi) {
				if (midiChunk.start > lastPosition) {
					// Found the next one
					index = newIndex
					currentRevision = revision
				}
				newIndex++
			}

			// Couldn't relocate. Give up
			index = Int.MAX_VALUE
		}
	}

	/**
	 * A chunk of midi packets.
	 * They can happen at the same time, but are ensured to be in the correct order.
	 *
	 * @param id The unique ID of the midi event
	 * @param start Offset (in beats) this MidiChunk applies
	 * @param midi Array of midi packets
	 */
	class MidiChunk(
			val id: String,
			val start: Double,
			val midi: Array<ShortArray>
	) : Cloneable {
		public override fun clone(): MidiChunk {
			return MidiChunk(id, start, midi.map { it.clone() }.toTypedArray())
		}
	}

	private var dirty = false

	/**
	 * Increased every time a change has been made.
	 */
	private var revision = 0

	override fun iterator(): Iterator<IteratorItem> {
		return IteratorHandler(0.0)
	}

	public override fun clone(): MidiData {
		return MidiData(midi.map { it.clone() } as MutableList<MidiChunk>)
	}

	fun add(midiChunk: MidiChunk) {
		midi.add(midiChunk)
		dirty = true
		revision++
	}

	fun slice(from: Float = 0f, to: Float = Float.MAX_VALUE): MidiData {
		if (dirty)
			cleanUp()

		TODO()
	}

	fun merge(midiData: MidiData) {
		for (midiPacket in midiData.midi)
			midi.add(midiPacket)

		revision++
	}

	/**
	 * Removes overlaying duplicates, zero-length notes, and then sorts.
	 */
	fun cleanUp() {
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
	}

	/**
	 * Flattens all non-midi like bezier curves to MIDI CC data points.
	 * Like for use by the backend, that doesn't deal with curves.
	 * @param pointsPerSecond How many MIDI data points to write for each second
	 */
	fun flattenToMidi(pointsPerSecond: Int) {

	}
}
