package net.merayen.elastic.backend.architectures.local.nodes.oscilloscope_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import kotlin.math.max
import kotlin.math.roundToInt

class LProcessor : LocalProcessor() {
	var samples = FloatArray(200)

	/**
	 * Where in the samples-array we will write next time.
	 */
	private var samplesPosition = -1

	/**
	 * Position where we sampled last time from the input signal
	 */
	private var inputPosition = 0

	private var beenBelowTrigger = false

	/**
	 * Last time we triggered, in samples
	 */
	private var nextForcedTrigging = 0L

	private var trigging = false

	private var frameFinished = false

	var minValue = 0f
		private set

	var maxValue = 0f
		private set

	var samplesAvailable = false
		private set

	override fun onPrepare() {
		frameFinished = false
		samplesAvailable = false
	}

	override fun onProcess() {
		if (frameFinished || !available())
			return

		val inlet = getInlet("in") ?: return

		val lnode = localNode as LNode
		val amplitude = lnode.amplitude
		val offset = lnode.offset
		val time = lnode.time
		val trigger = lnode.trigger

		// Calculate for every input sample to sample
		val sampleEvery = max(1, (sampleRate * time / samples.size).roundToInt())

		val input = when (inlet) {
			is AudioInlet -> inlet.outlet.audio[0]
			is SignalInlet -> inlet.outlet.signal
			else -> return
		}

		minValue = Float.MAX_VALUE
		maxValue = Float.MIN_VALUE

		for (sample in input) {
			if (sample < minValue)
				minValue = sample
			if (sample > maxValue)
				maxValue = sample
		}

		for ((i, sample) in input.withIndex()) {
			if (!trigging) {
				if (nextForcedTrigging < System.currentTimeMillis()) {
					trigging = true
					inputPosition = 0
					samplesPosition = 0
					beenBelowTrigger = false
				} else if (sample * amplitude + 0.01f < trigger) {
					beenBelowTrigger = true
				} else if (beenBelowTrigger && sample >= trigger) {
					trigging = true
					inputPosition = 0
					samplesPosition = 0
					beenBelowTrigger = false
				}
			}

			if (trigging) {
				if (inputPosition++ % sampleEvery == 0) {
					samples[samplesPosition++] = (sample + offset) * amplitude
					if (samplesPosition == samples.size) { // Done sampling our frame
						samplesPosition = -1
						beenBelowTrigger = false
						nextForcedTrigging = System.currentTimeMillis() + 100 // We never trigger less than 2 times a second
						trigging = false
						samplesAvailable = true
						break
					}
				}
			}
		}

		frameFinished = true
	}

	override fun onInit() {}
	override fun onDestroy() {}
}