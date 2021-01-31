package net.merayen.elastic.ui.objects.top.window

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.util.ImmutablePoint
import java.util.*

class EasyMotionOverlay(private val window: Window) : UIObject() {
	var isHelping = false

	val random = Random()
	override fun onDraw(draw: Draw) {
		draw.disableOutline()
		draw.setColor(1f, 0f, 1f)

		val stack = window.easyMotion.getCurrentStack()
		val size = stack.size

		for ((i, entry) in stack.withIndex()) {
			val active = i + 1 == size
			val uiobject = entry.easyMotionBranch.outline

			val pos = getRelativePosition(uiobject) ?: return

			draw.setStroke(2f)
			if (active)
				draw.setColor(1f, 0f, 1f)
			else
				draw.setColor(0.5f, 0f, 0.5f)

			draw.rect(pos.x, pos.y, uiobject.getWidth(), uiobject.getHeight())

			if (isHelping && active) {

				draw.setFont("", 16f)
				var listed = 0
				for ((keys, control) in entry.easyMotionBranch.controls.entries) {
					if (keys.isEmpty()) continue

					val target = control.target

					val targetPos = if (target != null) {
						getRelativePosition(target) ?: continue
					} else {
						ImmutablePoint(pos.x, pos.y + 20f * listed++)
					}

					val text = keys.toString()

					draw.setColor(0f, 0f, 0f, 0.7f)
					draw.fillRect(targetPos.x, targetPos.y, draw.getTextWidth(text) + 4, 20f)

					for (c in 0 until 2) {
						draw.setColor(c * 1f, c * 1f, c * 1f, 0.7f)
						draw.text(text, targetPos.x + c * 2, targetPos.y + c * 2 + 16f)
					}
				}
			}
		}
	}

}