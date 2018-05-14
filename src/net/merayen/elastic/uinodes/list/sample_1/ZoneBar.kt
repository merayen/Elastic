package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class ZoneBar : UIObject() {
	var layoutWidth = 10f

	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.fillRect(0f, 0f, layoutWidth, 10f)
	}
}