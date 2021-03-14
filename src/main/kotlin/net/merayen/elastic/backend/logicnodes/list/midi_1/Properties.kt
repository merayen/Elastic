package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(
	var eventZones: EventZones? = null,
	var mute: Boolean? = null,
	var solo: Boolean? = null,
	var record: Boolean? = null,
	var trackName: String? = null
) : BaseNodeProperties() {
	data class EventZone(
		var id: String? = null,
		var start: Float? = null, // Relative to us
		var length: Float? = null,
		var midi: MidiData? = null
	)

	class EventZones : ArrayList<EventZone>() {
		/**
		 * Get all the MidiMessages flat, where all the start positions are made absolute (not relative to EventZone).
		 *
		 * Returns the MidiMessages sorted by their start positions. Does not clone midi data itself.
		 */
		fun getAbsoluteMidiMessages(): List<MidiData.MidiMessage> {
			val result = ArrayList<MidiData.MidiMessage>()

			for (eventZone in this)
				for (midiMessage in eventZone.midi!!)
					result.add(
						MidiData.MidiMessage(midiMessage.id, midiMessage.start!! + eventZone.start!!, midiMessage.midi)
					)

			return result.sortedBy { it.start }
		}
	}

	init {
		classRegistry.add(EventZone::class)
		classRegistry.add(MidiData::class)
		classRegistry.add(MidiData.MidiMessage::class)
	}
}
