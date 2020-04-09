package net.merayen.elastic.backend.architectures.local.nodes.arithmetic_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.arithmetic_1.Mode

class LProcessor : LocalProcessor() {
	override fun onProcess() {
		if (!available())
			return

		val aInlet = getInlet("a")
		val bInlet = getInlet("b")
		val outlet = getOutlet("out") as? AudioOutlet ?: return


		// Only supports audio for now. Support more!
		if ((aInlet == null && bInlet == null) || aInlet.format != Format.AUDIO || aInlet.format != Format.AUDIO) {
			for (channel in outlet.audio)
				for (i in channel.indices)
					channel[i] = 0f

			outlet.push()

			return
		}

		val aAudioInlet = aInlet as? AudioInlet
		val bAudioInlet = bInlet as? AudioInlet

		val localNode = localNode as LNode

		val aChannels = aAudioInlet?.outlet?.audio
		val bChannels = bAudioInlet?.outlet?.audio

		when (localNode.mode) {
			Mode.ADDITION -> {
				when {
					aChannels == null -> {
						outlet.audio = bChannels // Forward whole buffer
					}
					bChannels == null -> {
						outlet.audio = aChannels
					}
					else -> { // Both ports available
						val out = outlet.audio
						for ((channelIndex, channel) in aChannels.withIndex()) {
							TODO()
							for (i in 0 until buffer_size)
								out[channelIndex][i] = channel[i]
						}
					}
					}
				}
			Mode.SUBTRACTION -> TODO()
			Mode.MULTIPLICATION -> TODO()
			Mode.DIVISION -> TODO()
			Mode.MODULO -> TODO()
		}
	}

	override fun onPrepare() {}
	override fun onInit() {}
	override fun onDestroy() {}
}