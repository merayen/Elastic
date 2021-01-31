package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class Checkbox : UIObject() {
	var whenChanged: (() -> Unit)? = null

	val label = Label()
	var checked = false

	private val mouseHandler = MouseHandler(this, MouseEvent.Button.LEFT)

	override fun onInit() {
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseClick(position: MutablePoint?) {
				checked = checked xor true
				whenChanged?.invoke()
			}
		})
		label.translation.x = 18f
		add(label)
	}

	override fun onDraw(draw: Draw) {
		if (checked) {
			draw.setColor(20, 20, 20)
			draw.fillRect(0f, 0f, 12f, 12f)
		}

		draw.setStroke(1f)
		draw.setColor(150, 150, 150)
		draw.rect(0f, 0f, 12f, 12f)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}