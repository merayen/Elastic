package net.merayen.elastic.backend.architectures.local.nodes.frequency_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import net.merayen.elastic.util.math.fft.FFT
import java.lang.Integer.min
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

class LProcessor : LocalProcessor() {
	private var done = false
	var spectrum: FloatArray? = null
	var accumulator: FloatArray? = null
	var accumulatorPosition = 0

	override fun onInit() {
		val lnode = localNode as LNode
		accumulator = FloatArray(lnode.accumulatorSize)
		spectrum = FloatArray(lnode.spectrumSize)

	}

	override fun onPrepare() {
		done = false
	}

	override fun onProcess() {
		if (done || !available())
			return

		done = true

		val lnode = localNode as LNode
		if (lnode.collectSpectrum < System.currentTimeMillis())
			return // Haven't gotten a request to stream spectrum data for a while, so we stop doing it

		val input = getInlet("in")

		val samples = FloatArray(buffer_size)

		when (input) {
			is AudioInlet ->// Mix down all channels to mono
				for (channel in input.outlet.audio)
					if (channel != null)
						for (i in channel.indices)
							samples[i] += channel[i]
			is SignalInlet ->
				System.arraycopy(input.outlet.signal, 0, samples, 0, buffer_size)
			else -> // Unknown format
				return
		}

		// Copy to accumulator
		val accumulator = accumulator!!
		for (i in 0 until min(buffer_size, accumulator.size - accumulatorPosition))
			accumulator[accumulatorPosition++] = samples[i] * lnode.window[i]

		// If accumulator has been filled, we do a FFT on it
		if (accumulatorPosition == accumulator.size) {
			val imx = FloatArray(accumulator.size)
			FFT.fft(accumulator, imx)

			val spectrum = spectrum!!
			val scale = (accumulator.size / 2.0) / spectrum.size

			for (i in 0 until accumulator.size / 2)
				spectrum[(i / scale).toInt()] += ((accumulator[i].pow(2f) + imx[i].pow(2f)).pow(0.5f) - spectrum[(i / scale).toInt()]) / 100

			accumulatorPosition = 0
		}
	}

	override fun onDestroy() {}
}