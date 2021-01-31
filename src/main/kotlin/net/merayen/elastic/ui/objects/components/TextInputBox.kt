package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

/**
 * A clickable text input.
 * Use textInput freely, directly.
 * TODO implement scrolling after the cursor
 */
class TextInputBox : UIClip() {
	val textInput = TextInput()

	private val mouseHandler = MouseHandler(this)

	override fun onInit() {
		super.onInit()
		add(textInput)

		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseClick(position: MutablePoint?) {
				focus()
			}
		})
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(0.2f, 0.2f, 0.2f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setStroke(2f)
		draw.setColor(0.5f, 0.5f, 0.5f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}

	fun focus() = textInput.focus()
}