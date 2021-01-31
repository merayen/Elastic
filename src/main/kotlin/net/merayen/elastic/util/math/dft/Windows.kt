package net.merayen.elastic.util.math.dft

import kotlin.math.PI
import kotlin.math.cos

class Windows {
	companion object {
		fun hamming(result: FloatArray) {
			val pi = PI.toFloat()

			for (i in result.indices)
				result[i] = 0.54f - 0.46f * cos(2 * pi * i / (result.size - 1))
		}
	}
}