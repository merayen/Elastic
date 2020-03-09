package net.merayen.elastic.backend.architectures.local.nodes.histogram_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet

class LProcessor : LocalProcessor() {

	/**
	 * Width of the histogram.
	 * Set by the LNode.
	 */
	var width = 2f

	/**
	 * Where the histogram lowest value starts.
	 * Set by the LNode.
	 */
	var start = -1f

	/**
	 * How many bins.
	 * Set by the LNode.
	 */
	var resolution = 100

	/**
	 * Maximum value read in current frame
	 */
	var maxValue = 0f

	/**
	 * Minimum value read in current frame
	 */
	var minValue = 0f

	var buckets = FloatArray(0)
		private set

	override fun onInit() {}
	override fun onPrepare() {
		if (buckets.size != resolution) {
			buckets = FloatArray(resolution)
		} else {
			for (i in 0 until resolution)
				buckets[i] = 0f
		}

		minValue = Float.MAX_VALUE
		maxValue = Float.MIN_VALUE
	}

	override fun onProcess() {
		val dataPort = getInlet("data") ?: return

		val available = dataPort.available()

		if (!dataPort.available())
			return

		if (dataPort is AudioInlet) {
			for (channel in 0 until dataPort.outlet.audio.size) {
				val audio = dataPort.outlet.audio[channel] ?: continue

				for (i in 0 until buffer_size) {
					val sample = audio[i]
					if ((sample > 0.01f || sample < -0.01) && sample >= start && sample < start+width)
						buckets[((sample - start) / width * (resolution-1)).toInt()] += 1f

					if (sample < minValue)
						minValue = sample
					else if (sample > maxValue)
						maxValue = sample
				}
			}
		}
	}

	override fun onDestroy() {}
}