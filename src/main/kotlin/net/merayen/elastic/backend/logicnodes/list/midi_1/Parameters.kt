package net.merayen.elastic.backend.logicnodes.list.midi_1

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class Parameters(private val logicNode: LogicNode) {
	class EventZone(val map: HashMap<String, Any> = HashMap()) {
		var id: String by map
		var start: Float by map
		var length: Float by map
	}

	class TangentEvent(val map: HashMap<String, Any> = HashMap()) {
		var id: String by map
		var eventZoneId: String by map
		var start: Float by map
		var length: Float by map

		/**
		 * Midi to be dispatched when tangent begins
		 */
		var startMidi: List<Short> by map

		/**
		 * Midi to be dispatched when tangent ends
		 */
		var stopMidi: List<Short> by map
	}

	class ControlEvent(val map: HashMap<String, Any> = HashMap()) {
		var id: String by map
		var eventZoneId: String by map
		var start: Float by map
		var midi: List<Short> by map
	}

	fun setEventZones(eventZones: List<EventZone>) = logicNode.set("eventZones", eventZones.map { it.map }.toList())

	fun setTangentEvents(tangentEvents: List<TangentEvent>) = logicNode.set("tangentEvent", tangentEvents.map { it.map }.toList())

	fun setControlEvent(controlEvents: List<ControlEvent>) = logicNode.set("controlEvent", controlEvents.map { it.map }.toList())


	fun getEventZones(): ArrayList<EventZone> = ArrayList(retrieveEventZones().map { EventZone(it) })

	fun getTangentEvents(): ArrayList<TangentEvent> = ArrayList(retrieveTangentEvents().map { TangentEvent(it) })

	fun getControlEvents(): ArrayList<ControlEvent> = ArrayList(retrieveControlEvents().map { ControlEvent(it) })

	private fun retrieveEventZones(): List<HashMap<String, Any>> {
		var eventZones = logicNode.getParameter("eventZones") as? List<HashMap<String, Any>>
		if (eventZones == null) {
			eventZones = ArrayList()
			logicNode.set("eventZones", eventZones)
		}

		return eventZones
	}

	private fun retrieveTangentEvents(): List<HashMap<String, Any>> {
		var tangentEvents = logicNode.getParameter("tangentEvents") as? List<HashMap<String, Any>>
		if (tangentEvents == null) {
			tangentEvents = ArrayList()
			logicNode.set("tangentEvents", tangentEvents)
		}

		return tangentEvents
	}

	private fun retrieveControlEvents(): List<HashMap<String, Any>> {
		var controlEvents = logicNode.getParameter("controlEvents") as? List<HashMap<String, Any>>
		if (controlEvents == null) {
			controlEvents = ArrayList()
			logicNode.set("controlEvents", controlEvents)
		}

		return controlEvents
	}

	fun handle(key: String, value: Any) {
		when(key) {
			"eventZones" -> {

				logicNode.set("eventZones", value)
			}
		}
	}
}

fun main() {
	val eventZone = Parameters.EventZone(HashMap(mapOf("id" to "id123", "start" to 1.234f, "length" to 2.345f)))
	val controlEvent = Parameters.ControlEvent(HashMap(mapOf("id" to "id123", "start" to 1.234f, "length" to 2.345f, "eventZoneId" to "eventid", "midi" to shortArrayOf(1,2,3,4,5).toList())))
	val dumped = JSONObject.toJSONString(controlEvent.map)
	val loadedControlEvent = Parameters.ControlEvent(JSONParser().parse(dumped) as HashMap<String,Any>)
	println(loadedControlEvent.midi)
}