package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class NodeViewContainer : UIObject() {
	override fun onDraw(draw: Draw) {
		draw.empty(-10000000f, -10000000f, 10000000000f, 10000000000f)
	}
}
