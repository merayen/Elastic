package net.merayen.elastic.ui.objects.top.window

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.nodeview.find.AddNodeWindow
import java.util.*

class EasyMotionOverlay(private val window: Window) : UIObject() {
	val random = Random()
	override fun onDraw(draw: Draw) {
		draw.disableOutline()
		draw.setColor(1f, 0f, 1f)
		for (entry in window.easyMotion.getCurrentStack()) {
			val uiobject = entry.easyMotionBranch.outline
			if (uiobject is AddNodeWindow)
				System.currentTimeMillis()
			val pos = getRelativePosition(uiobject) ?: return

			draw.setStroke(2f)
			draw.rect(pos.x, pos.y, uiobject.getWidth(), uiobject.getHeight())
		}
	}
}