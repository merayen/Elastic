package net.merayen.elastic.backend.data.eventdata

import org.json.simple.JSONArray
import org.json.simple.JSONObject

/**
 * Common data class that is used several places.
 */
class MidiEventData : EventData() {
	class Zone : EventData.Zone() {
		/**
		 * All the midi packets in the zone
		 */
		var midi = ArrayList<MidiPacket>()
	}

	class MidiPacket(
			/**
			 * Offset into the zone this MidiPacket is fired
			 */
			var start: Float,

			/**
			 * The midi
			 */
			var midi: Array<Array<Short>>
	)

	val zones = ArrayList<Zone>()

	override fun dump(): JSONObject {
		val jsonObject = JSONObject()

		val zonesArray = JSONArray()
		jsonObject["zones"] = zonesArray
		for (zone in zones) {
			val zoneObject = JSONObject()

			zoneObject["start"] = zone.start
			zoneObject["length"] = zone.length
			zoneObject["midi"] = zone.midi

			zonesArray.add(zoneObject)
		}

		return jsonObject
	}

	override fun restore(json: JSONObject) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
