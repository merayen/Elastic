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

	override fun onEvent(event: UIEvent) {
		if(event is MouseEvent && event.id == id) {
			if(event.action == MouseEvent.Action.OUT_OF_RANGE) {
				enabled = false
			} else {
				enabled = true
				translation.x = event.x.toFloat()
				translation.y = event.y.toFloat()
			}
		}
	}
}