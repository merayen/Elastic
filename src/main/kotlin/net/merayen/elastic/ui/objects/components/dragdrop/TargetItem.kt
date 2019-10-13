package net.merayen.elastic.ui.objects.components.dragdrop

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem
import net.merayen.elastic.ui.objects.top.mouse.MouseCursorManager
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

abstract class TargetItem(val target: UIObject) : MouseHandler(target) {
	private val mouseCursorManager: MouseCursorManager by lazy {
		(target.search.top as Top).mouseCursorManager
	}

	private var mouseDown = false
	private var currentItem: MouseCarryItem? = null
	private var interested = false

	init {
		setHandler(object : MouseHandler.Handler() {
			override fun onMouseOver() {
				if(!mouseDown)
					return

				val item = mouseCursorManager.retrieveCarryItem(mouseEvent.id)
				if(item != null) {
					currentItem = item
					onHover(item)
				}
			}

			override fun onMouseOut() {
				if(!mouseDown)
					return

				val item = mouseCursorManager.retrieveCarryItem(mouseEvent.id)

				if(item != null)
					onBlur()

				currentItem = null
			}

			override fun onMouseUp(position: MutablePoint?) {
				val item = currentItem

				if(item != null) {
					onDrop(item)
					onBlurInterest()
					onBlur()
				}

				currentItem = null
			}

			override fun onGlobalMouseMove(global_position: MutablePoint?) {
				if(interested || !mouseDown)
					return

				val item = mouseCursorManager.retrieveCarryItem(mouseEvent.id)
				if(item != null) {
					interested = true
					onInterest(item)
				}
			}

			override fun onGlobalMouseUp(global_position: MutablePoint?) {
				mouseDown = false
				if(interested) {
					interested = false
					onBlurInterest()
					onBlur()
				}
			}

			override fun onMouseOutsideDown(global_position: MutablePoint?) {
				mouseDown = true
			}
		})
	}

	abstract fun onInterest(item: MouseCarryItem)
	abstract fun onBlurInterest()
	abstract fun onHover(item: MouseCarryItem)
	abstract fun onBlur()
	abstract fun onDrop(item: MouseCarryItem)
}