package net.merayen.elastic.ui.objects.components.oscilloscope

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import kotlin.math.max

class SignalDisplay : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	/**
	 * Set this to an array consisting of amplitude levels per sample.
	 * Feel free to set your internal array here, as it won't be changed by this class.
	 */
	var samples: FloatArray? = null
		set(value) {
			field = value
			lastUpdate = System.currentTimeMillis()
		}

	private var lastUpdate = System.currentTimeMillis()

	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		// Should probably have a canvas of some kind that we draw on with some transparency to keep some history?
		val samples = samples
		if (samples != null) {
			val intensity = max(0f, 1f - (System.currentTimeMillis() - lastUpdate) / 1000f)
			draw.setColor(0.4f * intensity, intensity, 0.4f * intensity)

			for ((i, p) in samples.withIndex())
				if (p in -1f..1f) // Manual clipping instead of using UIClip
					draw.fillRect((i / samples.size.toFloat()) * layoutWidth, (1 - (p / 2 + 0.5f)) * layoutHeight, 2f, 2f)
		}
	}
}