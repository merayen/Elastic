package net.merayen.elastic.backend.architectures.local.nodes.value_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.SignalOutlet

class LProcessor : LocalProcessor() {
	private var currentTime = 0.0
	private var done = false

	override fun onProcess() {
		val elapsed = getOutlet("elapsed") as? SignalOutlet
		val beat = getOutlet("beat") as? SignalOutlet

		if (elapsed != null) { // Time in seconds sine the
			var t = currentTime
			val increase = 1.0 / sampleRate
			for (i in elapsed.signal.indices) {
				elapsed.signal[i] = t.toFloat()
				t += increase
			}

			currentTime = t

			elapsed.push()
		}

		if (beat != null) {
			val parentGroup = localNode.parentGroupNode
			var beatPosition = parentGroup.getBeatPosition()
			val bpm = parentGroup.getCurrentFrameBPM()
			val increment = (bpm / 60.0) / sampleRate

			for (i in beat.signal.indices) {
				beat.signal[i] = beatPosition.toFloat()
				beatPosition += increment
			}

			beat.push()
		}

		done = true
	}

	override fun onPrepare() {
		done = false
	}

	override fun onInit() {}
	override fun onDestroy() {}
}