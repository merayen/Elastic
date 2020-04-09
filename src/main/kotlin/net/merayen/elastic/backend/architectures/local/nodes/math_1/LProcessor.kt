package net.merayen.elastic.backend.architectures.local.nodes.math_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet
import net.merayen.elastic.backend.architectures.local.lets.Inlet
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.math_1.Mode

class LProcessor : LocalProcessor() {
	private var aValue = 0f
	private var bValue = 0f

	override fun onProcess() {

		val aInlet = getInlet("a")
		val bInlet = getInlet("b")
		val outlet = getOutlet("out") as? AudioOutlet ?: return

		if ((aInlet != null || bInlet != null) && !available())
			return

		// Only supports audio for now. Support more!
		if (aInlet.format != Format.AUDIO || aInlet.format != Format.AUDIO) {
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
		val outChannels = outlet.audio

		val channelCount = localNode.parentGroupNode.getChannelCount()

		val mode = localNode.mode

		for (channel in 0 until channelCount) { // This loop may have bullshit performance. Developer lazy atm of writing. Mayhaps Kotlin or the JVM can see the pattern here?
			for (i in 0 until buffer_size) {
				outChannels[channel][i] = when (mode) {
					Mode.ADD -> (aChannels?.get(channel)?.get(i) ?: aValue) + (bChannels?.get(channel)?.get(i) ?: bValue)
					Mode.SUBTRACT -> (aChannels?.get(channel)?.get(i) ?: aValue) - (bChannels?.get(channel)?.get(i) ?: bValue)
					Mode.MULTIPLY -> (aChannels?.get(channel)?.get(i) ?: aValue) * (bChannels?.get(channel)?.get(i) ?: bValue)
					Mode.DIVIDE -> (aChannels?.get(channel)?.get(i) ?: aValue) / (bChannels?.get(channel)?.get(i) ?: bValue)
					Mode.MODULO -> TODO()
					Mode.LOG -> TODO()
					Mode.SIN -> TODO()
					Mode.COS -> TODO()
					Mode.TAN -> TODO()
					Mode.ASIN -> TODO()
					Mode.ACOS -> TODO()
					Mode.ATAN -> TODO()
					Mode.POWER -> TODO()
				}
			}
		}

		outlet.push()
	}

	override fun onPrepare() {}
	override fun onInit() {}
	override fun onDestroy() {}
}