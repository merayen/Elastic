package net.merayen.elastic.uinodes.list.frequency_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.util.Pacer
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

class FrequencySpectrum : UIObject(), FlexibleDimension {  // TODO move out as a generic reusable component
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private var poles = FloatArray(0)
	private val pacer = Pacer()

	override fun onUpdate() {
		super.onUpdate()
		pacer.update()

		// Reduce the poles every frame
		val diff = pacer.getDiff(0.99f)
		for (i in poles.indices)
			poles[i] *= (1 - diff)
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.setStroke(1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0f, 0.5f, 0.5f)
		val poleWidth = (layoutWidth * 0.95f) / poles.size
		for (i in poles.indices) {
			val value = 1 - log10(max(0.001f, min(poles[i], 1f))) * 10 / -30
			val x = i / (poles.size + 1f) * layoutWidth
			val height = value * layoutHeight
			draw.fillRect(x, layoutHeight - height, poleWidth, height)
		}
	}

	/**
	 * Apply a new set of poles.
	 */
	fun applyPoles(poles: FloatArray) {
		this.poles = poles.clone()
	}
}