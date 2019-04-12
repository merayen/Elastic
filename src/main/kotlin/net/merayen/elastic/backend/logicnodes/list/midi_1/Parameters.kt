package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.nodes.BaseLogicNodeParameters

class Parameters : BaseLogicNodeParameters() {
	class EventZone {
		class MidiEvent {
			var start = 0f
			var midi = shortArrayOf()
		}

		val midiEvents = ArrayList<MidiEvent>()
		var start = 0f
		var length = 0f
	}

	class TangentEvent {
		var eventZoneId = ""
		var start = 0f
		var length = 0f
		var midi = shortArrayOf()
	}

	class ControlEvent {
		var eventZoneId = ""
		var start = 0f
		var midi = shortArrayOf()
	}

	val eventZones = ArrayList<EventZone>()
	val tangentEvents = ArrayList<TangentEvent>()
	val controlEvent = ArrayList<ControlEvent>()
}