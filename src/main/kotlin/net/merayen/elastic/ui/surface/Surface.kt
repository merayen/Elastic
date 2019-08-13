package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.util.Point

abstract class Surface(val id: String, protected var handler: Handler) {

	abstract val width: Int
	abstract val height: Int

	/**
	 * Return the location of the surface (e.g window), if applicable.
	 * If not, returns Point(0, 0).
	 */
	abstract val surfaceLocation: Point

	interface Handler {
		fun onDraw(graphics2d: java.awt.Graphics2D)
	}

	interface NativeUI {
		interface MouseCursor {
			/**
			 * Set the global mouse pointer location, in pixels.
			 */
			fun setPosition(point: Point)

			/**
			 * Get the current global mouse pointer location, in pixels.
			 */
			fun getPosition(): Point
			fun hide()
			fun show()
		}

		interface Dialog {
			fun showTextInput(description: String = "", value: String = "", onDone: (value: String?) -> Unit)
		}

		val mouseCursor: MouseCursor
		val dialog: Dialog
	}

	/**
	 * Surface must implement this one
	 */
	abstract val nativeUI: NativeUI

	abstract fun pullEvents(): List<UIEvent>

	abstract fun end()

	abstract fun isReady(): Boolean
}
