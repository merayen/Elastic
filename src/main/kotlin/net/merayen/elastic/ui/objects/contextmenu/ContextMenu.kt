package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.objects.top.Window
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.UINodeUtil
import net.merayen.elastic.util.Point

/**
 * Puts up a context menu on top of everything (Top().overlay)
 */
class ContextMenu(trigger: UIObject, count: Int, button: MouseEvent.Button) {
	interface Handler {
		fun onSelect(item: ContextMenuItem?, position: Point)
		fun onMouseDown(position: Point)
	}

	var handler: Handler? = null
	var backgroundColor: Color
		set(value) {
			menu.backgroundColor = value
		}
		get() = menu.backgroundColor

	private val menu: Menu = Menu(count)
	private val mouse: MouseHandler = MouseHandler(trigger, button)

	constructor(trigger: UIObject, button: MouseEvent.Button) : this(trigger, 8, button)

	init {
		mouse.setHandler(object : MouseHandler.Handler() {
			var relative: Point? = null
			private var window: Window? = null

			var originalMousePointerLocation: Point? = null

			override fun onMouseDown(position: Point) {
				val window = UINodeUtil.getWindow(trigger)
				this.window = window

				if (window == null)
					return

				window.overlay.add(menu)

				relative = position

				moveNativeMouseCursorPosition()

				menu.translation.x = window.screenWidth / 2f
				menu.translation.y = window.screenHeight / 2f

				menu.radius = Math.min(window.screenWidth, window.screenHeight) / 4

				menu.animate()
				handler?.onMouseDown(position)
			}

			override fun onMouseDrag(position: Point, offset: Point) {
				val absolute = menu.absolutePosition

				if (absolute != null)
					menu.setPointer(mouse.mouseEvent.x - absolute.x, mouse.mouseEvent.y - absolute.y)
			}

			override fun onGlobalMouseUp(position: Point) {
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
				mouseCursor.setPosition(Point(surfaceLocation.x + window.screenWidth / 2f, surfaceLocation.y + window.screenHeight / 2f))

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
