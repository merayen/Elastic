package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

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
		fun onEventRepeat(eventId: String, count: Int)
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
		val test = createEventZone(UniqueID.create())
		test.start = 4f
		test.length = 1f

		zoomFactor = 50f
	}

	fun createEventZone(eventZoneId: String): EventZone {
		val event = EventZone(eventZoneId)

		event.handler = object : EventZone.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onMove(position: Float) {
				handler?.onChangeEvent(eventZoneId, position, event.length)
			}

			override fun onRepeatDrag() {
				val eventRepeater = EventDragMarker()
				eventRepeater.translation.x = event.translation.x + event.layoutWidth
				eventRepeater.layoutHeight = layoutHeight

				this.eventDragMarker = eventRepeater
				add(eventRepeater)
			}

			override fun onRepeatMove(count: Int) {
				eventDragMarker!!.layoutWidth = count * event.layoutWidth
			}

			override fun onRepeatDrop(count: Int) {
				val eventRepeater = eventDragMarker

				if (eventRepeater != null) {
					remove(eventRepeater)
					this.eventDragMarker = null
					handler?.onEventRepeat(eventZoneId, count)
				}
			}
		}

		event.translation.y = 2f
		events.put(eventZoneId, event)
		add(event)

		return event
	}

	override fun onUpdate() {
		for (event in events.values)
			event.layoutHeight = layoutHeight - 4f
	}

	fun getEvent(eventId: String): EventZone? {
		return events[eventId]
	}
}