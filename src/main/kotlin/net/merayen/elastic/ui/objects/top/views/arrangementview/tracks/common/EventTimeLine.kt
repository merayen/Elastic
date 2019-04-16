package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.backend.logicnodes.list.midi_1.Parameters
import net.merayen.elastic.util.UniqueID

/**
 * Event editor. Edit events. Events.
 */
class EventTimeLine : BaseTimeLine() {
	interface Handler {
		/**
		 * Called when an event has been moved.
		 */
		fun onChangeEvent(eventId: String, position: Float, length: Float)

		/**
		 * Called when an event should be repeated.
		 */
		fun onRepeatEvent(eventId: String, count: Int)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var handler: Handler? = null

	var zoomFactor = 0f
		set(value) {
			field = value
			for (event in events.values)
				event.zoomFactor = value
		}

	private val events = HashMap<String,EventZone>()

	/**
	 * Temporary events that are only used to show the user that the event are being repeated
	 */
	private val repeatEvents = ArrayList<EventZone>()

	override fun onInit() {
		/*val test = createEventZone(UniqueID.create())
		test.start = 1f
		test.length = 1f*/

		zoomFactor = 100f
	}

	fun createEventZone(eventZoneId: String): EventZone {
		val event = EventZone(eventZoneId)

		event.handler = object : EventZone.Handler {
			override fun onChange(start: Float, length: Float) {
				handler?.onChangeEvent(eventZoneId, start, length)
			}

			override fun onRepeat(count: Int) {
				handler?.onRepeatEvent(eventZoneId, count)
			}
		}

		event.translation.y = 2f
		events.put(eventZoneId, event)
		add(event)

		return event
	}

	fun clearEventZones() {
		for (event in events.values)
			remove(event)

		events.clear()
	}

	override fun onUpdate() {
		for (event in events.values)
			event.layoutHeight = layoutHeight - 4f
	}

	fun getEvent(eventId: String): EventZone? {
		return events[eventId]
	}

	fun loadEventZones(eventZones: List<Parameters.EventZone>) {
		clearEventZones()

		for (event in eventZones) {
			val e = createEventZone(event.id)
			e.start = event.start
			e.length = event.length
		}
	}
}