package net.merayen.elastic.ui.objects.components.oscilloscope

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class SignalDisplay : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	/**
	 * Set this to an array consisting of amplitude levels per sample.
	 * Feel free to set your internal array here, as it won't be changed by this class.
	 */
	var samples: FloatArray? = null

	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		// Should probably have a canvas of some kind that we draw on with some transparency to keep some history?
		val samples = samples
		if (samples != null) {
			draw.setColor(1f, 1f, 0f)

			for ((i, p) in samples.withIndex())
				if (p in 0f..1f) // Manual clipping instead of using UIClip
					draw.rect((i / samples.size.toFloat()) * layoutWidth, (1 - p) * layoutHeight, 1f, 1f)
		}
	}
}