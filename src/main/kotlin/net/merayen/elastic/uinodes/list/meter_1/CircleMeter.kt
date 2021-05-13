package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.ui.Draw
import kotlin.math.*

internal class CircleMeter : MeterBase() {
	var radiusOffset = PI / 4
	var range = PI * 1.5

	init {
		layoutWidth = 200f
		layoutHeight = 200f
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0f, 0f, 0f)
		draw.fillOval(0f, 0f, layoutWidth, layoutHeight)

		val value = if (maxValue > minValue)
			max(0f, min(1f, (value - minValue) / (maxValue - minValue)))
		else
			0f

		// Draw number circle
		draw.setColor(1f, 1f, 0f)
		draw.setFont(null, 12f)
		for (i in 0 until 11) {
			val x = sin(-(i / 10f) * range - radiusOffset).toFloat()
			val y = cos(-(i / 10f) * range - radiusOffset).toFloat()
			val textValue = "%.1f".format(i / 10f * (maxValue - minValue) + minValue)

			draw.text(
				textValue,
				layoutWidth / 2 + x * layoutWidth / 2.5f - draw.getTextWidth(textValue) / 2f,
				layoutHeight / 2 + y * layoutHeight / 2.5f + 6f
			)
		}

		val x = sin(-value * range - radiusOffset).toFloat()
		val y = cos(-value * range - radiusOffset).toFloat()

		draw.setColor(1f, .3f, .3f)
		draw.setStroke(4f)
		draw.line(
			layoutWidth / 2,
			layoutHeight / 2,
			layoutWidth / 2 + layoutWidth / 2.25f * x,
			layoutHeight / 2 + layoutHeight / 2.25f * y
		)
		draw.fillOval(layoutWidth / 2 - 5f, layoutHeight / 2 - 5f, 10f, 10f)
	}
}