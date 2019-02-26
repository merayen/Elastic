package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.UINodeUtil
import net.merayen.elastic.util.Point

/**
 * Puts up a context menu on top of everything (Top().overlay)
 */
class ContextMenu(trigger: UIObject, count: Int, button: MouseEvent.Button, handler: Handler) {
	private val menu: Menu = Menu(count)
	private val mouse: MouseHandler = MouseHandler(trigger, button)

	interface Handler {
		fun onSelect(item: ContextMenuItem?, position: Point)
	}

	constructor(trigger: UIObject, button: MouseEvent.Button, handler: Handler) : this(trigger, 8, button, handler)

	init {
		mouse.setHandler(object : MouseHandler.Handler() {
			var relative: Point? = null

			var originalMousePointerLocation: Point? = null

			override fun onMouseDown(position: Point) {
				val window = UINodeUtil.getWindow(trigger)
				window.overlay.add(menu)

				moveNativeMouseCursorPosition()
				relative = position
				menu.translation.x = window.screenWidth / 2f
				menu.translation.y = window.screenHeight / 2f

				menu.setPointer(0f, 0f)

				menu.radius = Math.min(window.screenWidth, window.screenHeight) / 4
			}

			override fun onMouseDrag(position: Point, offset: Point) {
				val window = UINodeUtil.getWindow(trigger)
				window.overlay.getRelativePosition(menu)
				val absolute = menu.absolutePosition

				menu.setPointer(mouse.mouseEvent.x - absolute.x, mouse.mouseEvent.y - absolute.y)
			}

			override fun onGlobalMouseUp(position: Point) {
				if (menu.parent != null) {
					UINodeUtil.getWindow(trigger).overlay.remove(menu)
					val selected = menu.getSelected()
					val rel = relative
					if (selected != null && rel != null)
						handler.onSelect(selected, rel)

					restoreNativeMouseCursorPosition()
				}
			}

			private fun moveNativeMouseCursorPosition() {
				val window = UINodeUtil.getWindow(trigger)
				val mouseCursor = UINodeUtil.getWindow(trigger).nativeUI.mouseCursor

				originalMousePointerLocation = mouseCursor.getPosition()

				val surfaceLocation = window.surfaceLocation
				mouseCursor.setPosition(Point(surfaceLocation.x + window.screenWidth / 2f, surfaceLocation.y + window.screenHeight / 2f))

				mouseCursor.hide()
			}

			private fun restoreNativeMouseCursorPosition() {
				val loc = originalMousePointerLocation
				val mouseCursor = UINodeUtil.getWindow(trigger).nativeUI.mouseCursor
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
