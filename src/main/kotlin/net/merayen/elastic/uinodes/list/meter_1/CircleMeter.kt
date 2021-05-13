package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.ui.Draw

internal class CircleMeter : MeterBase() {
	init {
		layoutWidth = 200f
		layoutHeight = 200f
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0f, 0f, 0f)
		draw.oval(0f, 0f, layoutWidth, layoutHeight)
	}
}