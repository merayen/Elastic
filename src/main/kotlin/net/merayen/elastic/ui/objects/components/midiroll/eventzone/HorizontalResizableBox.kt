package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.Movable
import net.merayen.elastic.util.MutablePoint


/**
 * Adds a resizable, and movable box onto an item.
 */
class HorizontalResizableBox : UIObject(), FlexibleDimension {
	interface Handler {
		fun onChange(offsetStart: Float, offsetLength: Float)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	inner class Side : UIObject() {
		override fun onDraw(draw: Draw) {
			//draw.empty(0f, 0f, 5f, layoutHeight)

			draw.setColor(1f, 0f, 1f)
			draw.setStroke(1f)
			draw.rect(0f, 0f, 5f, layoutHeight)
		}
	}

	inner class MoveBox : UIObject(), FlexibleDimension {
		override var layoutWidth = 0f
		override var layoutHeight = 0f

		override fun onDraw(draw: Draw) {
			draw.setColor(1f, 0.8f, 0f, 0.5f)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		}
	}

	var handler: Handler? = null

	var scaleX = 1f
	var scaleY = 1f

	private val mouseHandler = MouseHandler(this)
	private val right = Side()
	private val left = Side()
	private val movableRight = Movable(right, right)
	private val movableLeft = Movable(left, left)
	private val moveBox = MoveBox()

	private var moving = false
	private var resizing = false

	override fun onInit() {
		movableLeft.setHandler(object : Movable.IMoveable {
			override fun onGrab() {
				resizing = true
				add(moveBox)
			}

			override fun onMove() {
				moveBox.translation.x = left.translation.x
			}

			override fun onDrop() {
				handler?.onChange(left.translation.x, 0f)
				resizing = false
				remove(moveBox)
			}
		})

		movableRight.setHandler(object : Movable.IMoveable {
			override fun onGrab() {
				resizing = true
				add(moveBox)
			}

			override fun onMove() {}

			override fun onDrop() {
				handler?.onChange(0f, right.translation.x - 5 - layoutWidth)
				resizing = false
				remove(moveBox)
			}
		})

		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
				//handler?.onChange(offset.x, 0f)
			}
		})

		add(right)
		add(left)
	}

	override fun onUpdate() {
		if (!resizing) {
			left.translation.x = 0f
			left.translation.y = 0f
			right.translation.x = layoutWidth - 10
			right.translation.y = 0f
		}
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.9f, 0.9f, 0.5f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
		movableLeft.handle(event)
		movableRight.handle(event)
	}
}