package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.dialogs.TextInputDialog
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class TextInput : UIObject(), FlexibleDimension {
	interface Handler {
		fun onChange()
	}

	override var layoutWidth = 50f
	override var layoutHeight = 15f

	var text = ""
	var description = ""

	private val mouseHandler = MouseHandler(this, MouseEvent.Button.LEFT)

	override fun onInit() {
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseClick(position: Point?) {
				val dialog = TextInputDialog()
				add(dialog)
			}
		})
	}

	override fun onDraw(draw: Draw) {
		draw.setStroke(1f)
		draw.setColor(0.8f, 0.8f, 0.8f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0.3f, 0.3f, 0.3f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}