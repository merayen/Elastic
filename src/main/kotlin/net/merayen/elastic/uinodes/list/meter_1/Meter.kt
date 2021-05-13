package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class Meter : UIObject(), FlexibleDimension {
	override var layoutWidth = 200f
	override var layoutHeight = 20f

	override fun onDraw(draw: Draw) {
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
	}
}