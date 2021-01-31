package net.merayen.elastic.backend.architectures.local.nodes.frequency_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.frequency_1.FrequencyInputFrameData
import net.merayen.elastic.backend.logicnodes.list.frequency_1.FrequencyOutputFrameData
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.util.math.dft.Windows
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

class LNode : LocalNode(LProcessor::class.java) {
	var collectSpectrum = 0L
	val spectrumScale = 10

	val accumulatorSize: Int
		get() {
			return 2.0.pow(log2(sample_rate / 10.0).roundToInt()).roundToInt()
		}

	val spectrumSize: Int
		get() {
			return accumulatorSize / 2 / spectrumScale
		}

	lateinit var window: FloatArray
		private set

	override fun onInit() {
		window = FloatArray(accumulatorSize)
		Windows.hamming(window)
	}

	override fun onSpawnProcessor(lp: LocalProcessor?) {}

	override fun onProcess(data: InputFrameData?) {
		if (data is FrequencyInputFrameData) // UI wants to receive spectrum data, it says
			collectSpectrum = System.currentTimeMillis() + 1500
	}

	override fun onParameter(instance: BaseNodeProperties?) {}

	override fun onFinishFrame() {
		val spectrum = FloatArray(spectrumSize)
		val processorSpectrums = sessions.mapNotNull { (getProcessor(it) as? LProcessor)?.spectrum }
		for (processorSpectrum in processorSpectrums)
			for (i in spectrum.indices)
				spectrum[i] += processorSpectrum[i]

		outgoing = FrequencyOutputFrameData(id, spectrum)
	}

	override fun onDestroy() {}

}