package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent.Button
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class Resizable(private val node: UINode, private val handler: Handler) : UIObject() {
	private var mouseHandler: MouseHandler? = null

	private var startWidth: Float = 0.toFloat()
	private var startHeight: Float = 0.toFloat()

	interface Handler {
		fun onResize()
	}

	override fun onInit() {
		mouseHandler = MouseHandler(this, Button.LEFT)
		mouseHandler!!.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: Point) {
				startWidth = node.getWidth()
				startHeight = node.getHeight()
			}

			override fun onMouseDrag(position: Point, offset: Point) {
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
			draw.line(node.getWidth() - 2 * i, node.getHeight(), node.getWidth(), node.getHeight() - 2 * i)
			i += 2
		}
	}

	override fun onEvent(e: UIEvent) {
		mouseHandler!!.handle(e)
	}
}
