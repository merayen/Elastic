package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.backend.logicnodes.list.midi_1.Parameters
import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.util.Point
import net.merayen.elastic.util.UniqueID
import kotlin.math.max

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
		fun onRepeatEvent(id: String, count: Int)

		/**
		 * Signals that an event should be deleted
		 */
		fun onRemoveEvent(id: String)

		/**
		 * Called when to create a new event
		 */
		fun onCreateEvent(id: String, start: Float, length: Float)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var handler: Handler? = null

	var zoomFactor = 100f
		set(value) {
			field = value
			for (event in events.values)
				event.zoomFactor = value
		}

	private val events = HashMap<String,EventZone>()

	private var selectedEventZoneIds = ArrayList<String>()

	private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)

	override fun onInit() {
		super.onInit()

		contextMenu.handler = object : ContextMenu.Handler {
			override fun onMouseDown(position: Point) {}

			override fun onSelect(item: ContextMenuItem?, position: Point) {
				handler?.onCreateEvent(UniqueID.create(), position.x / zoomFactor, 1f)
			}
		}

		contextMenu.backgroundColor = Color(0.3f, 0.3f, 0.3f)

		contextMenu.addMenuItem(TextContextMenuItem("Add"))
	}

	fun createEventZone(eventZoneId: String): EventZone {
		val event = EventZone(eventZoneId)

		event.handler = object : EventZone.Handler {
			override fun onSelect() {
				selectedEventZoneIds.clear()
				selectedEventZoneIds.add(event.id)
				updateSelections()
			}

			override fun onChange(start: Float, length: Float) {
				handler?.onChangeEvent(eventZoneId, start, length)
			}

			override fun onRepeat(count: Int) {
				handler?.onRepeatEvent(eventZoneId, count)
			}

			override fun onRemove() {
				handler?.onRemoveEvent(event.id)
			}
		}

		event.translation.y = 2f
		event.layoutHeight = layoutHeight - 4f
		event.zoomFactor = zoomFactor
		events.put(eventZoneId, event)
		add(event)

		return event
	}

	private fun clearEventZones() {
		for (event in events.values)
			remove(event)

		events.clear()
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.empty(0f, 0f, layoutWidth, layoutHeight) // Makes ContextMenu work. Or not.
	}

	override fun onUpdate() {
		var layoutWidth = 0f
		for (event in events.values) {
			event.layoutHeight = layoutHeight - 4f
			layoutWidth = max(layoutWidth, event.translation.x + event.layoutWidth)
		}
		this.layoutWidth = max(layoutWidth, minimumWidth)
	}

	override fun onEvent(event: UIEvent) {
		super.onEvent(event)

		contextMenu.handle(event)
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

		updateSelections()
	}

	private fun updateSelections() {
		for (event in events.values) {
			if (event.id in selectedEventZoneIds) {
				remove(event)
				add(event)
				event.selected = true
			} else {
				event.selected = false
			}
		}
	}
}