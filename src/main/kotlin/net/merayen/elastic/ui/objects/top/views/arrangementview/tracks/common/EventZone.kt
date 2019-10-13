package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.Movable
import net.merayen.elastic.util.MutablePoint
import kotlin.math.max

class EventZone(val id: String) : UIObject(), FlexibleDimension {
	interface Handler {
		/**
		 * When user has moved an event (and let it go)
		 */
		fun onChange(start: Float, length: Float)

		/**
		 * When user let go of the event repeat-box
		 */
		fun onRepeat(count: Int)

		/**
		 * When event has been selected. Should be moved to the top in the draw list
		 */
		fun onSelect()

		/**
		 * EventZone should be deleted
		 */
		fun onRemove()

		/**
		 * User wants to edit event
		 */
		fun onEdit()
	}

	private class DragBox : UIObject(), FlexibleDimension {
		interface Handler {
			fun onDrag()
			fun onMove(xOffset: Float)
			fun onDrop(xOffset: Float)
		}

		private val mouseHandler = MouseHandler(this, MouseEvent.Button.LEFT)

		var handler: Handler? = null

		override var layoutWidth = 0f
		override var layoutHeight = 0f

		override fun onInit() {
			mouseHandler.setHandler(object : MouseHandler.Handler() {
				override fun onMouseDown(position: MutablePoint?) {
					handler?.onDrag()
				}

				override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
					handler?.onMove(offset.x)
				}

				override fun onMouseDrop(position: MutablePoint, offset: MutablePoint) {
					handler?.onDrop(offset.x)
				}
			})
		}

		override fun onDraw(draw: Draw) {
			draw.setColor(0.7f, 0.7f, 0.7f)
			draw.fillRect(0f, 0f, 10f, 10f)
		}

		override fun onEvent(event: UIEvent) {
			mouseHandler.handle(event)
		}
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var selected = false

	var handler: Handler? = null

	private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
	private val movable = Movable(this, this, MouseEvent.Button.LEFT)

	private val startBox = DragBox()
	private val lengthBox = DragBox()
	private val repeatBox = DragBox()

	private var moving = false
	var start = 0f // Bars
	var length = 0f // Length
	var zoomFactor = 10f // 1 == 1 bar is 1 unit

	override fun onInit() {
		translation.y = 2f

		contextMenu.handler = object : ContextMenu.Handler {
			override fun onMouseDown(position: MutablePoint) {
				handler?.onSelect()
			}

			override fun onSelect(item: ContextMenuItem?, position: MutablePoint) {
				if (item is TextContextMenuItem) {
					when (item.text) {
						"Remove" -> handler?.onRemove()
						"Edit" -> handler?.onEdit()
					}
				}
			}
		}

		contextMenu.backgroundColor = Color(0.3f, 0.3f, .5f)

		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(TextContextMenuItem("Remove"))
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(TextContextMenuItem("Edit"))

		val self = this
		movable.setHandler(object : Movable.IMoveable {
			var start = 0f
			override fun onGrab() {
				moving = true
				start = self.translation.x
				handler?.onSelect()
			}

			override fun onMove() {
				translation.y = 2f
			}

			override fun onDrop() {
				if (self.translation.x != start)
					handler?.onChange(max(0f, translation.x / zoomFactor), length)
			}
		})

		repeatBox.handler = object : DragBox.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onDrag() {
				val eventDragMarker = EventDragMarker()
				eventDragMarker.translation.x = layoutWidth
				eventDragMarker.layoutHeight = layoutHeight

				this.eventDragMarker = eventDragMarker
				add(eventDragMarker)

				handler?.onSelect()
			}

			override fun onMove(xOffset: Float) {
				val count = ((xOffset / zoomFactor) / length).toInt()
				eventDragMarker!!.layoutWidth = count * layoutWidth
			}

			override fun onDrop(xOffset: Float) {
				val eventDragMarker = eventDragMarker!!

				val count = ((xOffset / zoomFactor) / length).toInt()
				remove(eventDragMarker)
				this.eventDragMarker = null

				if (count > 0)
					handler?.onRepeat(count)
			}
		}

		lengthBox.handler = object : DragBox.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onDrag() {
				val eventDragMarker = EventDragMarker()
				eventDragMarker.translation.x = layoutWidth
				eventDragMarker.layoutHeight = layoutHeight

				this.eventDragMarker = eventDragMarker
				add(eventDragMarker)

				handler?.onSelect()
			}

			override fun onMove(xOffset: Float) {
				val eventDragMarker = eventDragMarker!!

				eventDragMarker.translation.x = 0f
				eventDragMarker.layoutWidth = layoutWidth + xOffset
			}

			override fun onDrop(xOffset: Float) {
				val eventDragMarker = eventDragMarker

				if (eventDragMarker != null) {
					val newLength = max(1 / 16f, length + xOffset / zoomFactor)
					remove(eventDragMarker)
					this.eventDragMarker = null
					handler?.onChange(start, newLength)
				}
			}
		}

		startBox.handler = object : DragBox.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onDrag() {
				val eventDragMarker = EventDragMarker()
				eventDragMarker.layoutHeight = layoutHeight

				this.eventDragMarker = eventDragMarker
				add(eventDragMarker)

				handler?.onSelect()
			}

			override fun onMove(xOffset: Float) {
				val eventDragMarker = eventDragMarker!!

				eventDragMarker.translation.x = xOffset
				eventDragMarker.layoutWidth = layoutWidth - xOffset
			}

			override fun onDrop(xOffset: Float) {
				val eventDragMarker = eventDragMarker

				if (eventDragMarker != null) {
					val newStart = xOffset / zoomFactor
					remove(eventDragMarker)
					this.eventDragMarker = null
					handler?.onChange(start + newStart, max(1 / 16f, length - newStart))
				}
			}
		}

		add(startBox)
		add(lengthBox)
		add(repeatBox)
	}

	override fun onDraw(draw: Draw) {
		if (selected)
			draw.setColor(0.4f, 0.4f, .6f)
		else
			draw.setColor(0.2f, 0.2f, .5f)

		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		if (selected)
			draw.setColor(0.8f, 0.8f, 0.8f)
		else
			draw.setColor(0.5f, 0.5f, 0.5f)

		draw.setStroke(2f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		if (!moving) {
			layoutWidth = length * zoomFactor
			translation.x = start * zoomFactor
		}

		startBox.translation.x = 2f
		startBox.translation.y = layoutHeight - 10f - 2f
		lengthBox.translation.x = layoutWidth - 10f - 2f
		lengthBox.translation.y = layoutHeight - 10f - 2f
		repeatBox.translation.x = layoutWidth - 10f - 2f
		repeatBox.translation.y = layoutHeight / 2 - 5f
	}

	override fun onEvent(event: UIEvent) {
		contextMenu.handle(event)
		movable.handle(event)
	}
}