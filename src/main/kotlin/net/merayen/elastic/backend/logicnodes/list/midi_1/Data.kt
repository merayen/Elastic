package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.nodes.BaseNodeData
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

data class Data(
		var eventZones: MutableList<EventZone>? = null,
		var mute: Boolean? = null,
		var solo: Boolean? = null,
		var record: Boolean? = null,
		var trackName: String? = null
) : BaseNodeData() {
	data class EventZone(
			var id: String,
			var start: Float,
			var length: Float,
			var midi: MidiData
	)

	init {
		classRegistry.add(EventZone::class)
	}
}
