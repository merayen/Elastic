package net.merayen.elastic.ui.objects.top.mouse

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent

class MouseCursor(val surfaceMouseCursors: SurfaceMouseCursors, val id: Int) : UIObject() {
	var carry: MouseCarryItem? = null
		set(value) {
			val v = field

			if(v != null)
				remove(v)

			if(value != null) {
				add(value)
				value.translation.x = 5f
				value.translation.y = 5f
			}

			field = value
		}

	var enabled = true

	override fun onEvent(e: UIEvent) {
		if(e is MouseEvent && e.id == id) {
			if(e.action == MouseEvent.Action.OUT_OF_RANGE) {
				enabled = false
			} else {
				enabled = true
				translation.x = e.x.toFloat()
				translation.y = e.y.toFloat()
			}
		}
	}
}