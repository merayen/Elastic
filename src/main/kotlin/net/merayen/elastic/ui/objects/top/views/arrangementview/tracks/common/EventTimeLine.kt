package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.util.UniqueID

/**
 * Event editor. Edit events. Events.
 */
class EventTimeLine : BaseTimeLine() {
	interface Handler {
		fun onEventMove(eventId: String, position: Float)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var handler: Handler? = null

	var zoomFactor = 0f
		set(value) {
			field = value
			for (event in events)
				event.zoomFactor = value
		}

	private val events = ArrayList<Event>()

	override fun onInit() {
		val test = createEvent(UniqueID.create())
		test.start = 4f
		test.length = 1f

		zoomFactor = 50f
	}

	fun createEvent(eventId: String): Event {
		val event = Event(eventId)

		event.handler = object : Event.Handler {
			override fun onMove(position: Float) {
				handler?.onEventMove(eventId, position)
			}
		}

		event.translation.y = 2f
		events.add(event)
		add(event)

		return event
	}

	override fun onUpdate() {
		for (event in events)
			event.layoutHeight = layoutHeight - 4f
	}
}