package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.FileDropEvent
import net.merayen.elastic.ui.event.UIEvent

class NodeViewContainer : UIObject() {
	override fun onDraw(draw: Draw) {
		draw.empty(-10000000f, -10000000f, 10000000000f, 10000000000f)
	}

	override fun onEvent(event: UIEvent) {
		if (event is FileDropEvent) {
			for (file in event.files) {
				val position = getRelativeFromAbsolute(event.x.toFloat(), event.y.toFloat())
				sendMessage(ImportFileIntoNodeGroupMessage(file, "test test todo fix plz", position.x, position.y))
			}
		}
	}
}
