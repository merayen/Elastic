package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.Movable

/**
 * Movable playhead cursor
 */
class Playhead : UIObject(), FlexibleDimension {
	interface Handler {
		/**
		 * Called when the playhead has been moved (user released it).
		 * @param position The current position in beats
		 */
		fun onMoved(position: Float)
	}

	var handler: Handler? = null
	var beatWidth = 10f

	override var layoutWidth = 20f
	override var layoutHeight = 20f

	private val movable = Movable(this, this)

	override fun onInit() {
		movable.setHandler(object : Movable.IMoveable {
			override fun onGrab() {}
			override fun onMove() {
				translation.y = 0f
			}
			override fun onDrop() {
				handler?.onMoved(translation.x / beatWidth)
			}

		})
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.1f, 0.1f, 0.1f)
		draw.polygon(floatArrayOf(
			0f, 0f,
			layoutWidth, 0f,
			layoutWidth / 2, 20f
		))

		draw.disableOutline()

		draw.setColor(0.2f, 0.2f, 0.2f)
		draw.line(layoutWidth / 2, 20f, layoutWidth / 2, layoutHeight)
		draw.setColor(0.8f, 0.8f, 0.8f)
		draw.line(layoutWidth / 2 + 1, 20f, layoutWidth / 2 + 1, layoutHeight)
	}

	override fun onEvent(event: UIEvent) {
		movable.handle(event)
	}
}