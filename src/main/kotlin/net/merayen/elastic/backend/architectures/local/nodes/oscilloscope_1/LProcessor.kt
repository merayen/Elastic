package net.merayen.elastic.backend.architectures.local.nodes.oscilloscope_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import kotlin.math.max
import kotlin.math.roundToInt

class LProcessor : LocalProcessor() {
	var samples = FloatArray(1000)

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
	 * Keeps track of if we have already sampled on this frame. We only sample once for each frame, as LNode
	 * only send the oscilloscope sample data once per frame processed.
	 */
	var doneSampling = false

	override fun onPrepare() {
		doneSampling = false
	}

	override fun onProcess() {
		if (!available() || doneSampling)
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

		for (sample in input) {
			if (samplesPosition == -1) { // We are ready to trigger, so we check if sample triggers
				if (sample < trigger)
					beenBelowTrigger = true
				else if (beenBelowTrigger && sample >= trigger) {
					println("Triggered ${System.currentTimeMillis() % 1000}")
					inputPosition = 0
					samplesPosition = 0
					beenBelowTrigger = false
				}
			}

			if (samplesPosition != -1) {
				if (inputPosition++ % sampleEvery == 0) {
					samples[samplesPosition++] = sample * amplitude + offset
					if (samplesPosition == samples.size) { // Done sampling our frame
						samplesPosition = -1
						doneSampling = true
						beenBelowTrigger = false
						break
					}
				}
			}
		}

		inputPosition %= sampleEvery
	}

	override fun onInit() {}
	override fun onDestroy() {}
}