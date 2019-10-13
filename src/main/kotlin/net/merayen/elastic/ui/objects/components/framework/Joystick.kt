package net.merayen.elastic.ui.objects.components.framework

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.BoxLabel
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class Joystick(private val handler: Handler) : UIObject() {
	interface Handler {
		fun onMove(x: Float, y: Float)
		fun onDrop()
		fun onLabel(x: Float, y: Float): String
	}

	val box = BoxLabel()

	private val mouseHandler = MouseHandler(box)

	var dragging = false
		private set

	override fun onInit() {
		add(box)

		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
				dragging = true
				handler.onMove(offset.x, offset.y)
				box.text = handler.onLabel(offset.x, offset.y)
			}

			override fun onMouseDrop(position: MutablePoint?, offset: MutablePoint?) {
				dragging = false
				handler.onDrop()
				box.text = handler.onLabel(0f, 0f)
			}
		})

		box.text = handler.onLabel(0f, 0f)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}