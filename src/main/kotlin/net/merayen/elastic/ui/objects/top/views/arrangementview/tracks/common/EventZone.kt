package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.Movable
import net.merayen.elastic.util.Point

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
				override fun onMouseDown(position: Point?) {
					handler?.onDrag()
				}

				override fun onMouseDrag(position: Point, offset: Point) {
					handler?.onMove(offset.x)
				}

				override fun onMouseDrop(position: Point, offset: Point) {
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

	var handler: Handler? = null

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

		movable.setHandler(object : Movable.IMoveable {
			override fun onGrab() {
				moving = true
			}

			override fun onMove() {
				translation.y = 2f
			}

			override fun onDrop() {
				handler?.onChange(translation.x / zoomFactor, length)
				moving = false
			}
		})

		repeatBox.handler = object : DragBox.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onDrag() {
				val eventRepeater = EventDragMarker()
				eventRepeater.translation.x = layoutWidth
				eventRepeater.layoutHeight = layoutHeight

				this.eventDragMarker = eventRepeater
				add(eventRepeater)
			}

			override fun onMove(xOffset: Float) {
				val count = ((xOffset / zoomFactor) / length).toInt()
				eventDragMarker!!.layoutWidth = count * layoutWidth
			}

			override fun onDrop(xOffset: Float) {
				val eventRepeater = eventDragMarker

				if (eventRepeater != null) {
					val count = ((xOffset / zoomFactor) / length).toInt()
					remove(eventRepeater)
					this.eventDragMarker = null
					handler?.onRepeat(count)
				}
			}
		}

		lengthBox.handler = object : DragBox.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onDrag() {
				val eventRepeater = EventDragMarker()
				eventRepeater.translation.x = layoutWidth
				eventRepeater.layoutHeight = layoutHeight

				this.eventDragMarker = eventRepeater
				add(eventRepeater)
			}

			override fun onMove(xOffset: Float) {
				val diff = ((xOffset / zoomFactor) / length)
				eventDragMarker!!.layoutWidth = diff * layoutWidth
			}

			override fun onDrop(xOffset: Float) {
				val eventRepeater = eventDragMarker

				if (eventRepeater != null) {
					val newLength = ((xOffset / zoomFactor) / length)
					remove(eventRepeater)
					this.eventDragMarker = null
					handler?.onChange(start, newLength)
				}
			}
		}

		startBox.handler = object : DragBox.Handler {
			private var eventDragMarker: EventDragMarker? = null

			override fun onDrag() {
				val eventRepeater = EventDragMarker()
				eventRepeater.layoutHeight = layoutHeight

				this.eventDragMarker = eventRepeater
				add(eventRepeater)
			}

			override fun onMove(xOffset: Float) {
				eventDragMarker!!.translation.x = xOffset
				eventDragMarker!!.layoutWidth = -xOffset
			}

			override fun onDrop(xOffset: Float) {
				val eventRepeater = eventDragMarker

				if (eventRepeater != null) {
					val newStart = xOffset / zoomFactor
					remove(eventRepeater)
					this.eventDragMarker = null
					handler?.onChange(start + newStart, length - newStart)
				}
			}
		}

		add(startBox)
		add(lengthBox)
		add(repeatBox)
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.5f, 0.5f, .8f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0.5f, 0.5f, 0.5f)
		draw.setStroke(1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		if (!moving) {
			layoutWidth = length * zoomFactor
			translation.x = start * zoomFactor
		}

		startBox.translation.y = layoutHeight - 10f
		lengthBox.translation.x = layoutWidth - 10f
		lengthBox.translation.y = layoutHeight - 10f
		repeatBox.translation.x = layoutWidth - 10f
		repeatBox.translation.y = layoutHeight / 2 - 5f
	}

	override fun onEvent(event: UIEvent) {
		movable.handle(event)
	}
}