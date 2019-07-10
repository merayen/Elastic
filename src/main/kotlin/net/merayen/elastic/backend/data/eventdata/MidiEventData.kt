package net.merayen.elastic.backend.data.eventdata

import org.json.simple.JSONArray
import org.json.simple.JSONObject

/**
 * Common data class that is used several places.
 */
class MidiEventData : EventData(), Cloneable {
	class Zone(val midiData: MidiData = MidiData()) : EventData.Zone(), Cloneable {
		public override fun clone(): Zone {
			return Zone(midiData.clone())
		}
	}

	val zones = ArrayList<Zone>()

	override fun dump(): JSONObject {
		val jsonObject = JSONObject()

		val zonesArray = JSONArray()
		jsonObject["zones"] = zonesArray
		for (zone in zones) {
			val zoneObject = JSONObject()

			zoneObject["start"] = zone.start
			zoneObject["length"] = zone.length
			zoneObject["midi"] = zone.midiData

			zonesArray.add(zoneObject)
		}

		return jsonObject
	}

	override fun restore(json: JSONObject) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	public override fun clone(): MidiEventData {
		val result = MidiEventData()
		result.zones.addAll(zones.map { it.clone() })
		return result
	}
}
