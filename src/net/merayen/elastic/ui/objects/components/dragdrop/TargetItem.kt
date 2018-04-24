package net.merayen.elastic.ui.objects.components.dragdrop

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

abstract class TargetItem(target: UIObject) : MouseHandler(target) {
	private val mouseCursorManager = (target.search.top as Top).mouseCursorManager
	private var currentItem: MouseCarryItem? = null

	init {
		setHandler(object : MouseHandler.Handler() {
			override fun onMouseOver() {
				val item = mouseCursorManager.retrieveCarryItem(mouseEvent.id)
				if(item != null) {
					currentItem = item
					onHover(item)
				}
			}

			override fun onMouseOut() {
				val item = mouseCursorManager.retrieveCarryItem(mouseEvent.id)

				if(item != null)
					onBlur()

				currentItem = null
			}

			override fun onMouseUp(position: Point?) {
				val item = currentItem

				if(item != null) {
					onDrop(item)
					onBlur()
				}

				currentItem = null
			}

			override fun onMouseOutsideDown(global_position: Point?) {
				val item = mouseCursorManager.retrieveCarryItem(mouseEvent.id)

				if(item != null)
					onInterest(item)
			}

			override fun onGlobalMouseUp(global_position: Point?) {
				onBlurInterest()
				onBlur()
			}
		})
	}

	abstract fun onInterest(item: MouseCarryItem)
	abstract fun onBlurInterest()
	abstract fun onHover(item: MouseCarryItem)
	abstract fun onBlur()
	abstract fun onDrop(item: MouseCarryItem)
}