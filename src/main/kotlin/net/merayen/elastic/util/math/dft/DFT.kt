package net.merayen.elastic.util.math.dft

import kotlin.math.*

/**
 * TODO make unit tests work
 */
class DFT(private val window: FloatArray) {
	fun handle(samples: FloatArray): FloatArray {
		if (samples.size != window.size)
			throw RuntimeException("DFT must be fed the same amount of data as the window is")

		val result = FloatArray(window.size / 2)

		val pi = PI.toFloat()

		for (i in window.indices)
			samples[i] *= window[i]

		for (pole in 0 until window.size / 2) {
			var re = 0f
			var im = 0f
			val step = max(1, (samples.size - pole) / 2)
			println(step)
			for (i in samples.indices step step) {
				val sample = samples[i]

				val c = cos(2 * pi * pole * i / samples.size)
				val s = sin(2 * pi * pole * i / samples.size)

				re += sample * c
				im += sample * s
			}
			result[pole] = (re.pow(2) + im.pow(2)).pow(0.5f) / (window.size / 2) * step
		}

		return result
	}
}