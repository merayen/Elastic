package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.ImmutableDimension
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.util.ImmutablePoint
import net.merayen.elastic.util.MutablePoint

abstract class Surface(val id: String, protected var handler: Handler) {
	/**
	 * Return the location of the surface (e.g window), if applicable.
	 * If not, returns Point(0, 0).
	 */
	abstract val surfaceLocation: MutablePoint

	interface Handler {
		fun onDraw(graphics2d: java.awt.Graphics2D)
	}

	interface NativeUI {
		interface MouseCursor {
			/**
			 * Set the global mouse pointer location, in pixels.
			 */
			fun setPosition(point: MutablePoint)

			/**
			 * Get the current global mouse pointer location, in pixels.
			 */
			fun getPosition(): MutablePoint

			fun hide()
			fun show()
		}

		interface Dialog {
			fun showTextInput(description: String = "", value: String = "", onDone: (value: String?) -> Unit)
		}

		interface Screen {
			/**
			 * Retrieve the size of the current screen.
			 *
			 * If the surface is a window, the surface which the top-left point of the window currently is gets
			 * returned.
			 */
			val activeScreenSize: ImmutableDimension
		}

		/**
		 * Get and set Window properties, if applicable
		 */
		interface Window {
			var position: ImmutablePoint
			var size: ImmutableDimension
			var isDecorated: Boolean
		}

		val screen: Screen
		val window: Window

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

	/**
	 * Returns the ID of the thread that is used to draw the UI.
	 */
	abstract val threadId: Long
}
