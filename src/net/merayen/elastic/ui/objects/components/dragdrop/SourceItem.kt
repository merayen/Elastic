package net.merayen.elastic.ui.objects.components.dragdrop

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

abstract class SourceItem(source: UIObject) : MouseHandler(source) {
	private val mouseCursorManager = (source.search.top as Top).mouseCursorManager

	init {
		setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: Point?) {
				mouseCursorManager.setCarryItem(mouseEvent.id, onGrab())
			}

			override fun onGlobalMouseUp(position: Point?) {
				mouseCursorManager.removeCarryItem(mouseEvent.id)
				onDrop()
			}
		})
	}

	abstract fun onGrab(): MouseCarryItem
	abstract fun onDrop()
}