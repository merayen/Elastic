package net.merayen.elastic.backend.architectures.local.nodes.cutoff_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import kotlin.math.max
import kotlin.math.min

class LProcessor : LocalProcessor() {
	private var pos = FloatArray(0)
	private var speed = FloatArray(0)

	override fun onInit() {}
	override fun onPrepare() {}

	override fun onProcess() {
		val audioIn = getInlet("in") as? AudioInlet
		val audioOut = getOutlet("out") as? AudioOutlet
		val frequencyIn = getInlet("frequency") // Can be AudioInlet and SignalInlet
		val dampingIn = getInlet("damping")

		val available = available()

		if (audioIn != null && audioOut != null) {
			if (available) {
				val lnode = (localNode as LNode)
				var frequency = lnode.frequency
				var damping = lnode.damping

				val channelCount = localNode.parentGroupNode.getChannelCount()

				if (channelCount != pos.size) {
					pos = FloatArray(channelCount)
					speed = FloatArray(channelCount)
				}

				var frequencyInData: FloatArray? = null
				if (frequencyIn != null) {
					when (frequencyIn) {
						is AudioInlet -> frequencyInData = frequencyIn.outlet.audio[0]
						is SignalInlet -> TODO()
					}
				}

				var dampingInData: FloatArray? = null
				if (dampingIn != null) {
					when (dampingIn) {
						is AudioInlet -> dampingInData = dampingIn.outlet.audio[0]
						is SignalInlet -> TODO()
					}
				}

				for (channel in 0 until channelCount) {
					val channelAudioIn = audioIn.outlet.audio[channel]
					val channelAudioOut = audioOut.audio[channel]

					var pos = pos[channel]
					var speed = speed[channel]

					for (i in 0 until buffer_size) {
						if (frequencyInData != null) // Slow?
							frequency = (2 + min(1f, max(-1f, frequencyInData[i]))) * 10

						if (dampingInData != null)
							damping = 1.001f + dampingInData[i] * 100

						val sample = channelAudioIn[i]
						speed += (sample - pos) / frequency
						speed /= damping
						pos += speed
						channelAudioOut[i] = pos
					}

					this.pos[channel] = pos
					this.speed[channel] = speed
				}

				audioOut.push()
			}
		} else {
			if (audioOut != null) {
				if (!audioOut.available())
					audioOut.push()
			}
		}
	}

	override fun onDestroy() {}
}