package net.merayen.elastic.ui

import net.merayen.elastic.ui.util.DrawContext
import java.util.*

/**
 * This runs in the main-thread (???), and represents everything UI, at least locally.
 * (Remote UI might be an option later on)
 */
class Supervisor(private val top: UIObject) {

	@Synchronized
	fun draw(drawContext: DrawContext) {
		internalDraw(drawContext, top)
		internalUpdate(drawContext, top)
	}

	/**
	 * Drawing of UIObjects. Only allowed to draw, not adding/removing UIObjects etc.
	 */
	private fun internalDraw(dc: DrawContext, uiobject: UIObject) {
		dc.push(uiobject)

		uiobject.draw_z = dc.pushZIndex()
		uiobject.absoluteTranslation = dc.translation_stack.absolute

		if (!uiobject.isInitialized)
			uiobject.initialize()

		val draw = Draw(uiobject, dc)

		uiobject.updateDraw(draw)

		uiobject.outline = draw.outline
		uiobject.absoluteOutline = draw.absoluteOutline

		draw.destroy()

		for (o in uiobject.onGetChildren(dc.surfaceID))
			internalDraw(dc, o)

		dc.pop()
	}

	/**
	 * Non-draw update of UIObjects.
	 * Here the UIObjects can change their properties, add/remove UIObjects, etc.
	 */
	private fun internalUpdate(dc: DrawContext, uiobject: UIObject) {
		if (uiobject.topMost !== top) // Only update UIObject if it is connected to our tree
			return

		if (uiobject.isInitialized) { // UIObject probably created in a previous onInit(), and has not been initialized yet, if this skips
			for (e in dc.incoming_events)
				uiobject.onEvent(e)

			uiobject.onUpdate()
		}

		for (o in ArrayList(uiobject.onGetChildren(dc.surfaceID)))
			internalUpdate(dc, o)
	}
}
