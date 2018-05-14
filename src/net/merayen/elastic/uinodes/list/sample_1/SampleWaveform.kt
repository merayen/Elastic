package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import kotlin.math.max
import kotlin.math.min

class SampleWaveform : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var waveForm: FloatArray? = null

	override fun onDraw(draw: Draw) {
		val waveForm = this.waveForm
		if (waveForm != null) {
			draw.setColor(200, 200, 0)
			var lastY = layoutHeight / 2
			var waveFormLength = waveForm.size.toFloat()
			for(x in 0..layoutWidth.toInt()) {
				val y = max(-1f, min( 1f, waveForm[((x / layoutWidth) * (waveFormLength - 1)).toInt()])) * (layoutHeight / 2) + layoutHeight / 2
				draw.line(x - 1f, lastY, x.toFloat(), y)
				lastY = y
			}
		}
	}
}