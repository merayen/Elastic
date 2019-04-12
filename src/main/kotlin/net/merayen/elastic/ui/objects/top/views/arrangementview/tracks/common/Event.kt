package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.Movable

class Event(val eventId: String) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onMove(position: Float)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var handler: Handler? = null

	private val movable = Movable(this, this, MouseEvent.Button.LEFT)
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
				handler?.onMove(translation.x / zoomFactor)
				moving = false
			}

		})
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.5f, 0.5f, .8f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0.7f, 0.7f, 0.7f)
		draw.fillRect(0f, layoutHeight - 10f, 10f, 10f)
		draw.fillRect(layoutWidth - 10f, layoutHeight - 10f, 10f, 10f)
		draw.fillRect(layoutWidth - 10f, layoutHeight / 2 - 5f, 10f, 10f)

		draw.setColor(0.5f, 0.5f, 0.5f)
		draw.setStroke(1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		if (!moving) {
			layoutWidth = length * zoomFactor
			translation.x = start * zoomFactor
		}
	}

	override fun onEvent(event: UIEvent) {
		movable.handle(event)
	}
}