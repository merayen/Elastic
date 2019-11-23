package net.merayen.elastic.util.math.dft

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * A single DFT to retrieve the amplitude of a single frequency in a signal.
 * Uses running statistics.
 *
 * @param inputRate At what rate the samples are. E.g 44100
 * @param frequency The frequency to detect
 * @param width The frequency spectrum pole width
 */
class SingleDFT(private val inputRate: Int, private val frequency: Int, private val width: Float) {  // TODO support width
	private var re = 0f
	private var im = 0f
	private var pos = 0
	private val pi = PI.toFloat()

	init {
		if (width < 1)
			throw RuntimeException("width parameter must be 1 or more")
	}

	fun handle(samples: FloatArray): FloatArray {
		val result = FloatArray((pos + samples.size) / frequency)
		var i = 0

		for (sample in samples) {
			re += sample * cos(2 * pi * pos / frequency)
			im += sample * sin(2 * pi * pos / frequency)
			pos++

			if (pos == frequency) {
				result[i++] = (re.pow(2) + im.pow(2)).pow(.5f) / frequency * 2
				pos = 0
			}
		}

		return result
	}
}