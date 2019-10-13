package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent.Button
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class Resizable(private val node: FlexibleDimension, private val handler: Handler) : UIObject() {
	private var mouseHandler: MouseHandler? = null

	private var startWidth: Float = 0.toFloat()
	private var startHeight: Float = 0.toFloat()

	interface Handler {
		fun onResize()
	}

	override fun onInit() {
		mouseHandler = MouseHandler(this, Button.LEFT)
		mouseHandler!!.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: MutablePoint) {
				startWidth = node.layoutWidth
				startHeight = node.layoutHeight
			}

			override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
				node.layoutWidth = startWidth + offset.x
				node.layoutHeight = startHeight + offset.y
				handler.onResize()
			}
		})
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(200, 200, 200)
		draw.setStroke(0.5f)
		var i = 1
		while (i < 8) {
			draw.line(node.layoutWidth - 2 * i, node.layoutHeight, node.layoutWidth, node.layoutHeight - 2 * i)
			i += 2
		}
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler!!.handle(event)
	}
}
