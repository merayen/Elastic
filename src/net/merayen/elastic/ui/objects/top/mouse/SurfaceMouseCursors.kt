package net.merayen.elastic.ui.objects.top.mouse

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.Top
import java.util.*

/**
Elastic is thought to be a bit special. It can have multiple mouse cursors for local co-op, even on the same surface.
This is done by e.g having gamepads and a mouse hooked up.
 */
class SurfaceMouseCursors : UIObject() {
	private lateinit var mouseCursorManager: MouseCursorManager
	val cursors = ArrayList<MouseCursor>()

	override fun onInit() {
		mouseCursorManager = (this.search.top as Top).mouseCursorManager
	}

	override fun onEvent(e: UIEvent) {
		if(e is MouseEvent)
			ensureCursor(e.id)
	}

	private fun ensureCursor(id: Int) {
		var cursor = cursors.find { it.id == id }
		if(cursor == null) {
			cursor = MouseCursor(this, id)
			add(cursor)
			cursors.add(cursor)
		}

		val newCarry = mouseCursorManager.retrieveCarryItem(id)

		if(newCarry != cursor.carry)
			cursor.carry = newCarry
	}
}