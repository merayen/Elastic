package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class ArrangementGrid : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var barWidth = 100f
	var division = 4

	override fun onDraw(draw: Draw) {
		draw.disableOutline()

		draw.setColor(0.3f, 0.3f, 0.3f)
		for (i in 0 until (layoutWidth / barWidth * division).toInt()) {
			if (i % division == 0)
				draw.setStroke(2f)
			else
				draw.setStroke(1f)

			val x = i.toFloat() * (barWidth / division)

			draw.line(x,0f, x, layoutHeight)
		}
	}
}