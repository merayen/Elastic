package net.merayen.elastic.backend.logicnodes.list.midi_1

import com.google.gson.Gson

class Parameters {
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

fun main() {
	val parameters = Parameters()
	val eventZone = Parameters.EventZone()
	eventZone.start = 5f
	parameters.eventZones.add(eventZone)

	val dump = Gson().toJson(parameters)
	println(dump)

	val readParameters = Gson().fromJson<Parameters>(dump, Parameters::class.java)
	println(readParameters.eventZones[0].start)
}