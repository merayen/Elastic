package net.merayen.elastic.util

import kotlin.math.min

class Pacer {
	private var last = 0L;
	private var diff = 0f
	private var started = false

	fun update() {
		if (started)
			diff = ((System.currentTimeMillis() - last) / 1000.0).toFloat()
		else
			started = true

		last = System.currentTimeMillis()
	}

	fun getDiff(factor: Float = 1f): Float {
		return min(diff * factor, 1f)
	}
}