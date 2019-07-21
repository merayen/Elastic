package net.merayen.elastic.uinodes.list.histogram_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import kotlin.math.min

internal class Histogram : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private var lastReduce = System.currentTimeMillis()

	var buckets: Array<Float>? = null
		set(value) {
			field = value?.clone()
		}


	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		val buckets = buckets ?: return
		val maxValue = buckets.max() ?: return

		if (maxValue > 0) {
			draw.setColor(0.8f, 0.6f, 0f)
			val size = buckets.size
			val barSize = layoutHeight / size

			for (i in 0 until size)
				if (buckets[i] > 0)
					draw.fillRect(0f, (layoutHeight / size) * i, layoutWidth * (buckets[i] / maxValue), barSize)
		}
	}

	override fun onUpdate() {
		val buckets = buckets

		if (buckets != null) {
			val diff = (System.currentTimeMillis() - lastReduce) / 1000f

			for (i in 0 until buckets.size)
				buckets[i] -= buckets[i] * min(1f, diff)

			lastReduce = System.currentTimeMillis()
		}
	}
}