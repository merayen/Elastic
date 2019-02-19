package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class PianoNet(private val octave_count: Int) : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var octaveWidth = (5 * 7).toFloat()

	var barWidth = 10f

	private val BLACK_TANGENTS = booleanArrayOf(false, true, false, true, false, false, true, false, true, false, true, false)

	override fun onDraw(draw: Draw) {
		var y = 0f

		draw.setStroke(0.5f)

		var pos = 0
		for (i in 0 until octave_count * 12) {
			val b = if (BLACK_TANGENTS[pos]) 1 else 0

			draw.setColor(50 - b * 20, 50 - b * 20, 50 - b * 20)

			draw.fillRect(0f, y, layoutWidth, octaveWidth / 12)

			draw.setColor(0, 0, 0)
			draw.rect(0f, y, layoutWidth, octaveWidth / 12)

			y += octaveWidth / 12
			pos++
			pos %= 12
		}

		drawBars(draw)

		layoutHeight = y
	}

	private fun drawBars(draw: Draw) {
		draw.setColor(0.1f, 0.1f, 0.1f)
		/*while () {

		}*/
	}
}
