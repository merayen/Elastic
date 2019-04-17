package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

internal class SelectionRectangle : UIObject(), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	override fun onDraw(draw: Draw) {
		draw.setColor(0.2f, 0.2f, 1f, 0.5f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setStroke(2f)
		draw.setColor(0.8f, 0.8f, 0.8f)
		draw.fillRect(2f, 2f, layoutWidth - 4f, layoutHeight - 4f)
	}
}