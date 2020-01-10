package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.Window
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.UINodeUtil
import net.merayen.elastic.util.MutablePoint

/**
 * Puts up a context menu on top of everything (Top().overlay)
 */
class ContextMenu(trigger: UIObject, count: Int, button: MouseEvent.Button) {
	interface Handler {
		fun onSelect(item: ContextMenuItem?, position: MutablePoint)
		fun onMouseDown(position: MutablePoint)
	}

	var handler: Handler? = null
	var backgroundColor: MutableColor
		set(value) {
			menu.backgroundColor = value
		}
		get() = menu.backgroundColor

	private val menu: Menu = Menu(count)
	private val mouse: MouseHandler = MouseHandler(trigger, button)

	constructor(trigger: UIObject, button: MouseEvent.Button) : this(trigger, 8, button)

	init {
		mouse.setHandler(object : MouseHandler.Handler() {
			var relative: MutablePoint? = null
			private var window: Window? = null

			var originalMousePointerLocation: MutablePoint? = null

			override fun onMouseDown(position: MutablePoint) {
				val window = UINodeUtil.getWindow(trigger)
				this.window = window

				if (window == null)
					return

				window.overlay.add(menu)

				relative = position

				moveNativeMouseCursorPosition()

				menu.translation.x = window.layoutWidth / 2f
				menu.translation.y = window.layoutHeight / 2f

				menu.radius = Math.min(window.layoutWidth, window.layoutHeight) / 4

				menu.animate()
				handler?.onMouseDown(position)
			}

			override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
				val absolute = menu.absolutePosition

				if (absolute != null)
					menu.setPointer(mouse.mouseEvent.x - absolute.x, mouse.mouseEvent.y - absolute.y)
			}

			override fun onGlobalMouseUp(position: MutablePoint) {
				if (menu.parent != null) {
					UINodeUtil.getWindow(trigger)!!.overlay.remove(menu)
					val selected = menu.getSelected()
					val rel = relative
					if (selected != null && rel != null)
						handler?.onSelect(selected, rel)

					restoreNativeMouseCursorPosition()
				}
			}

			private fun moveNativeMouseCursorPosition() {
				val window = window ?: return

				val mouseCursor = window.nativeUI.mouseCursor

				originalMousePointerLocation = mouseCursor.getPosition()

				val surfaceLocation = window.surfaceLocation
				mouseCursor.setPosition(MutablePoint(surfaceLocation.x + window.layoutWidth / 2f, surfaceLocation.y + window.layoutHeight / 2f))

				mouseCursor.hide()
			}

			private fun restoreNativeMouseCursorPosition() {
				val window = window ?: return

				val loc = originalMousePointerLocation

				val mouseCursor = window.nativeUI.mouseCursor
				if (loc != null)
					mouseCursor.setPosition(loc)

				mouseCursor.show()
				originalMousePointerLocation = null
			}
		})
	}

	fun handle(event: UIEvent) {
		mouse.handle(event)
	}

	fun addMenuItem(item: ContextMenuItem) {
		menu.addMenuItem(item)
	}
}
