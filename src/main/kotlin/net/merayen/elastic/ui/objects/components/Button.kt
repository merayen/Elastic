package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

open class Button(var label: String = "") : UIObject(), FlexibleDimension {
	override var layoutWidth = 50f
	override var layoutHeight = 15f
	var autoDimension = true
	var fontSize = 10f
	var textColor = Color()
	var backgroundColor = Color(0.5f, 0.5f, 0.5f)

	var handler: IHandler? = null
	private var mousehandler: MouseHandler? = null
	private var mouseDown: Boolean = false
	private var mouseOver: Boolean = false

	interface IHandler {
		fun onClick()
	}

	override fun onInit() {
		mousehandler = MouseHandler(this)
		mousehandler!!.setHandler(object : MouseHandler.Handler() {

			override fun onMouseUp(position: Point) {
				if (mouseDown && handler != null)
					handler!!.onClick()

				mouseDown = false
			}

			override fun onMouseOver() {
				mouseOver = true
			}

			override fun onMouseOut() {
				mouseOver = false
			}

			override fun onMouseDown(position: Point) {
				mouseDown = true
			}

			override fun onGlobalMouseUp(global_position: Point) {
				mouseDown = false
			}
		})
	}

	override fun onDraw(draw: Draw) {
		draw.setFont("Geneva", fontSize)

		val textWidth = draw.getTextWidth(label)
		if (autoDimension) {
			layoutWidth = textWidth + 10
			layoutHeight = fontSize * 1.5f
		}

		draw.setColor(0.2f, 0.2f, 0.2f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		if (mouseDown && mouseOver)
			draw.setColor(
					backgroundColor.red + (1f - backgroundColor.red) * 0.5f,
					backgroundColor.green + (1f - backgroundColor.green) * 0.5f,
					backgroundColor.blue + (1f - backgroundColor.blue) * 0.5f
			)
		else
			draw.setColor(backgroundColor)

		draw.fillRect(1f, 1f, layoutWidth - 2f, layoutHeight - 2f)

		draw.setColor(textColor)
		if (mouseDown && mouseOver)
			draw.text(label, layoutWidth / 2 - textWidth / 2, fontSize + fontSize / 5)
		else
			draw.text(label, layoutWidth / 2 - textWidth / 2, fontSize)
	}

	override fun onEvent(event: UIEvent) {
		mousehandler!!.handle(event)
	}
}