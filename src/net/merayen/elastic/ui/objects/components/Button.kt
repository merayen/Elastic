package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

open class Button(var label: String = "") : UIObject(), FlexibleDimension {
	override var layoutWidth = 50f
	override var layoutHeight = 15f
	var auto_dimension = true
	var font_size = 10f

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
		draw.setFont("Geneva", font_size)

		val text_width = draw.getTextWidth(label)
		if (auto_dimension) {
			layoutWidth = text_width + 10
			layoutHeight = font_size * 1.5f
		}

		draw.setColor(50, 50, 50)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		if (mouseDown && mouseOver)
			draw.setColor(80, 80, 80)
		else
			draw.setColor(120, 120, 120)
		draw.fillRect(1f, 1f, layoutWidth - 2f, layoutHeight - 2f)

		draw.setColor(200, 200, 200)
		draw.text(label!!, layoutWidth / 2 - text_width / 2, font_size)

		super.onDraw(draw)
	}

	override fun onEvent(event: UIEvent) {
		mousehandler!!.handle(event)
	}
}