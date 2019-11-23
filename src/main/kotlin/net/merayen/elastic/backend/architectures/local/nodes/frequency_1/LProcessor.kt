package net.merayen.elastic.backend.architectures.local.nodes.frequency_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet

class LProcessor : LocalProcessor() {
	override fun onInit() {}
	override fun onPrepare() {}

	override fun onProcess() {
		val input = getInlet("in")

		if (input is AudioInlet && input.available() == buffer_size) {
			val channels: Array<FloatArray?> = arrayOfNulls(input.outlet.audio.size)
			for (channel in channels.indices) {
				var step = 32
				while (step <= sample_rate / 2) {
					TODO("Run through DFT-class")
					step *= 2
				}
			}

		}
	}

	override fun onDestroy() {}
}