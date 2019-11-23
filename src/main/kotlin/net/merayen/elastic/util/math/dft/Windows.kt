package net.merayen.elastic.util.math.dft

import kotlin.math.PI
import kotlin.math.cos

class Windows {
	companion object {
		fun hamming(width: Int): FloatArray {
			val result = FloatArray(width)
			val pi = PI.toFloat()

			for (i in 0 until width)
				result[i] = 0.54f - 0.46f * cos(2 * pi * i / (width - 1))

			return result
		}
	}
}