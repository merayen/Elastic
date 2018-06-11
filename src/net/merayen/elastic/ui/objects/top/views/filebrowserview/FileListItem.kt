package net.merayen.elastic.ui.objects.top.views.filebrowserview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

internal open class FileListItem : UIObject() {
	private val mouseHandler = MouseHandler(this)
	private var over = false
	var label: String? = null
	private var width = 0f
	private var height = 0f
	private var handler: Handler? = null

	internal interface Handler {
		fun onClick()
	}

	fun setHandler(handler: Handler) {
		this.handler = handler
	}

	override fun onInit() {
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseOver() {
				over = true
			}

			override fun onMouseOut() {
				over = false
			}

			override fun onMouseClick(position: Point) {
				if (handler != null)
					handler!!.onClick()
			}
		})
	}

	override fun onDraw(draw: Draw) {
		val label = label ?: return

		draw.setFont("", 15f)
		width = draw.getTextWidth(label)
		height = 15f

		if (over)
			draw.setColor(150, 150, 150)
		else
			draw.setColor(50, 50, 50)
		draw.fillRect(-2f, -2f, width + 4, (15 + 4).toFloat())

		draw.setColor(255, 255, 255)
		draw.text(label, 0f, 10f)
	}

	override fun getWidth(): Float {
		return width
	}

	override fun getHeight(): Float {
		return height
	}

	override fun onEvent(e: UIEvent) {
		mouseHandler.handle(e)
	}
}
