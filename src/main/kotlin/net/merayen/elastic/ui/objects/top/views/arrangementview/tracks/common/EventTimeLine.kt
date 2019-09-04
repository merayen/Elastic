package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.boolean
import net.merayen.elastic.util.Point
import net.merayen.elastic.util.UniqueID
import kotlin.math.max

/**
 * Event editor. Edit events. Events.
 */
class EventTimeLine : BaseTimeLine() {
	interface Handler : BaseTimeLine.Handler {
		/**
		 * Called when an event has been moved.
		 */
		fun onChangeEvent(eventId: String, position: Float, length: Float)

		/**
		 * Called when an event should be repeated.
		 */
		fun onRepeatEvent(id: String, count: Int)

		/**
		 * Signals that an event should be deleted.
		 */
		fun onRemoveEvent(id: String)

		/**
		 * Called when to create a new event.
		 */
		fun onCreateEvent(id: String, start: Float, length: Float)

		/**
		 * User wants to edit an event.
		 */
		fun onEditEvent(id: String)

		/**
		 * Indicates that user has clicked on an event. Receiver of this event should decide if all selections
		 * should be cleared, or not (e.g when SHIFT-modifier key is held)
		 */
		fun onEventSelect()
	}

	override var layoutWidth = 0f

	override var layoutHeight = 0f
	var handler: Handler? = null

	override var beatWidth = 20f
		set(value) {
			field = value
			for (event in events.values)
				event.zoomFactor = value
		}

	private val events = HashMap<String, EventZone>()

	private var selectedEventZoneIds = ArrayList<String>()

	private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
	private val addEventZoneMenuItem = TextContextMenuItem("Add zone")
	private val graphMenuItem = TextContextMenuItem("Show graph")

	private val mouseHandler = MouseHandler(this, MouseEvent.Button.LEFT)

	private val eventTimeLineGraph = EventTimeLineGraph()

	override fun onInit() {
		super.onInit()

		contextMenu.handler = object : ContextMenu.Handler {
			override fun onMouseDown(position: Point) {}

			override fun onSelect(item: ContextMenuItem?, position: Point) {
				when (item) {
					addEventZoneMenuItem -> handler?.onCreateEvent(UniqueID.create(), position.x / beatWidth, 4f)
					graphMenuItem -> toggleGraph(true)
				}
			}
		}

		contextMenu.backgroundColor = Color(0.3f, 0.3f, 0.3f)

		contextMenu.addMenuItem(addEventZoneMenuItem)
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(graphMenuItem)

		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: Point?) {
				handler?.onEventSelect()
			}

			override fun onMouseDrag(position: Point, offset: Point) {
				handler?.onSelectionDrag(position, offset)
			}

			override fun onMouseDrop(position: Point, offset: Point) {
				handler?.onSelectionDrop(position, offset)
			}
		})

		eventTimeLineGraph.handler = object : EventTimeLineGraph.Handler {
			override fun onHide() {
				toggleGraph(false)
			}
		}
	}

	fun createEventZone(eventZoneId: String): EventZone {
		val event = EventZone(eventZoneId)

		event.handler = object : EventZone.Handler {

			override fun onSelect() {
				handler?.onEventSelect()
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

			override fun onEdit() {
				handler?.onEditEvent(event.id)
			}
		}

		event.translation.y = 2f
		event.layoutHeight = layoutHeight - 4f
		event.zoomFactor = beatWidth
		events[eventZoneId] = event
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

		eventTimeLineGraph.layoutWidth = layoutWidth
		eventTimeLineGraph.layoutHeight = layoutHeight
	}

	override fun onEvent(event: UIEvent) {
		super.onEvent(event)
		contextMenu.handle(event)
		mouseHandler.handle(event)
	}

	override fun clearSelections() {
		selectedEventZoneIds.clear()
		updateSelections()
	}

	fun getEvent(eventId: String): EventZone? {
		return events[eventId]
	}

	fun loadEventZones(eventZones: List<Properties.EventZone>) {
		clearEventZones()

		for (event in eventZones) {
			val e = createEventZone(event.id!!)
			e.start = event.start!!
			e.length = event.length!!
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

	override fun onSelectionRectangle(selectionRectangle: UIObject) {
		val pos = getRelativePosition(selectionRectangle) ?: return

		for (event in events.values) {
			val collision = boolean(
				Rect(pos.x, pos.y, pos.x + selectionRectangle.getWidth(), pos.y + selectionRectangle.getHeight()),
				Rect(event.translation.x, event.translation.y, event.translation.x + event.layoutWidth, event.translation.y + event.layoutHeight)
			)

			// TODO check for SHIFT-modifier
			if (collision.width > 0 && collision.height > 0) {
				if (event.id !in selectedEventZoneIds)
					selectedEventZoneIds.add(event.id)
			} else {
				selectedEventZoneIds.remove(event.id)
			}
		}

		updateSelections()
	}

	fun toggleGraph(show: Boolean) {
		if (show) {
			if (eventTimeLineGraph.parent == null)
				add(eventTimeLineGraph)
		} else {
			if (eventTimeLineGraph.parent != null)
				remove(eventTimeLineGraph)
		}
	}
}
