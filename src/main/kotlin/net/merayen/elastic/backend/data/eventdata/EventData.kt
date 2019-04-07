package net.merayen.elastic.backend.data.eventdata

import org.json.simple.JSONObject

abstract class EventData {
	abstract class Zone {
		/**
		 * Where the zone starts in bars.
		 */
		var start = 0.0

		/**
		 * Length of the zone. Midi notes or audio outside the range is muted
		 */
		var length = 0.0
	}

	abstract fun dump(): JSONObject
	abstract fun restore(json: JSONObject)
}