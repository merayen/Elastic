package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class EventDragMarker : UIObject(), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	override fun onDraw(draw: Draw) {
		draw.setColor(0.8f, 0.5f, 0.8f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
	}
}