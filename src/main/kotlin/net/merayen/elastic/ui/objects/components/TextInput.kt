package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.objects.dialogs.TextInputDialog
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class TextInput : UIClip(), FlexibleDimension {
	interface Handler {
		fun onChange(text: String)
	}

	override var layoutWidth = 50f
	override var layoutHeight = 15f

	var value = ""
	var description = ""
	var handler: Handler? = null

	private val mouseHandler = MouseHandler(this, MouseEvent.Button.LEFT)

	override fun onInit() {
		super.onInit()
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseClick(position: Point?) {
				val dialog = TextInputDialog(description, value) {
					if (it != null) {
						value = it
						handler?.onChange(it)
					}
				}
				add(dialog)
			}
		})
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setStroke(1f)
		draw.setColor(0.8f, 0.8f, 0.8f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0.3f, 0.3f, 0.3f)
		draw.rect(0f, 0f, layoutWidth - 1, layoutHeight - 1)

		draw.setColor(0, 0, 0)
		draw.setFont("", 10f)
		draw.text(value, 2f, layoutHeight - 4)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}