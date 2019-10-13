package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class SelectionRectangle(private val trigger: UIObject? = null) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onDrag()
		fun onDrop()
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private var mouseHandler: MouseHandler? = null

	private var shown = true

	var handler: Handler? = null

	override fun onInit() {
		if (trigger != null) {
			shown = false // Hide ourself

			val mouseHandler = MouseHandler(trigger, MouseEvent.Button.LEFT)

			mouseHandler.setHandler(object : MouseHandler.Handler() {
				private var startPosition: MutablePoint? = null

				override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
					shown = true

					val startPosition = startPosition ?: position
					this.startPosition = startPosition

					val x = if (offset.x >= 0) startPosition.x else startPosition.x + offset.x
					val y = if (offset.y >= 0) startPosition.y else startPosition.y + offset.y
					val width = if (offset.x >= 0) offset.x else -offset.x
					val height = if (offset.y >= 0) offset.y else -offset.y

					translation.x = x
					translation.y = y

					layoutWidth = width
					layoutHeight = height

					handler?.onDrag()
				}

				override fun onMouseDrop(position: MutablePoint, offset: MutablePoint) {
					shown = false
					startPosition = null

					handler?.onDrop()
				}
			})

			this.mouseHandler = mouseHandler
		}
	}

	override fun onDraw(draw: Draw) {
		if (shown) {
			draw.setColor(0.8f, 0.8f, 1f, 0.5f)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

			draw.setStroke(1f)
			draw.setColor(1f, 1f, 1f)
			draw.rect(1f, 1f, layoutWidth - 2f, layoutHeight - 2f)
		}
	}

	fun handle(event: UIEvent) {
		mouseHandler?.handle(event)
	}

	/**
	 * Cancels any ongoing selection rectangle.
	 */
	fun cancel() {
		shown = false
	}
}
