package net.merayen.elastic.backend.architectures.local.nodes.to_audio_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet

class LProcessor : LocalProcessor() {
	private var done = false
	override fun onProcess() {
		if (done || !available())
			return

		val inlet = getInlet("in")
		val outlet = getOutlet("out") as? AudioOutlet ?: return

		val channelCount = localNode.parentGroupNode.getChannelCount()

		if (inlet is SignalInlet) {
			val input = inlet.outlet.signal
			val output = outlet.audio

			for (channel in 0 until channelCount) // Apply signal to all audio channels (perhaps make this chooseable later some time)
				for (i in input.indices)
					output[channel][i] = input[i]
		}

		outlet.push()
		done = true
	}

	override fun onPrepare() {
		done = false
	}

	override fun onInit() {}
	override fun onDestroy() {}
}