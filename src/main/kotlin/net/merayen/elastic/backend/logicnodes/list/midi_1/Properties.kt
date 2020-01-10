package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(
	var eventZones: MutableList<EventZone>? = null,
	var mute: Boolean? = null,
	var solo: Boolean? = null,
	var record: Boolean? = null,
	var trackName: String? = null
) : BaseNodeProperties() {
	data class EventZone(
		var id: String? = null,
		var start: Float? = null,
		var length: Float? = null,
		var midi: MidiData? = null
	)

	init {
		classRegistry.add(EventZone::class)
		classRegistry.add(MidiData::class)
		classRegistry.add(MidiData.MidiChunk::class)
	}
}
