package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.nodes.BaseNodeData

data class Data(
	var eventZones: MutableList<EventZone>? = null,
	var mute: Boolean? = null,
	var solo: Boolean? = null,
	var record: Boolean? = null,
	var trackName: String? = null
) : BaseNodeData() {
	data class EventZone(
		var id: String? = null,
		var start: Float? = null,
		var length: Float? = null,
		var midi: MidiData? = null
	)

	init {
		classRegistry.add(EventZone::class)
	}
}
