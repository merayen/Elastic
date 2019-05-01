package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class PianoNet(private val octave_count: Int) : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	/**
	 * Vertical size of 1 octave, in height units
	 */
	var octaveWidth = (5 * 7).toFloat()

	/**
	 * How many width units one beat is
	 */
	var beatWidth = 10f

	private val BLACK_TANGENTS = arrayOf(false, true, false, true, false, true, false, false, true, false, true, false)

	override fun onDraw(draw: Draw) {
		drawBars(draw)
		drawLines(draw)
	}

	private fun drawLines(draw: Draw) {
		var y = octaveWidth / 12f / 2f

		draw.setStroke(0.5f)

		var pos = 0
		for (i in 0 until octave_count * 12) {
			val b = BLACK_TANGENTS[pos]

			if (b)
				draw.setColor(0.1f, 0.1f, 0.1f)
			else
				draw.setColor(0.5f, 0.5f, 0.5f)

			draw.line(0f, y, layoutWidth, y)

			y += octaveWidth / 12
			pos++
			pos %= 12
		}

		layoutHeight = y
	}

	private fun drawBars(draw: Draw) {
		draw.setStroke(1f)
		draw.setColor(0.1f, 0.1f, 0.1f)
		var x = 0f
		for (i in 0 until (layoutWidth / beatWidth).toInt() + 1) { // TODO don't use "0 until layoutWidth", only draw what is visible
			draw.line(x, 0f, x, layoutHeight)
			x += beatWidth
		}
	}
}
