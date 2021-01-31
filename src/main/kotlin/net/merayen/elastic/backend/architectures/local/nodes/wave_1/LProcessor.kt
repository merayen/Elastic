package net.merayen.elastic.backend.architectures.local.nodes.wave_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalOutlet
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.backend.midi.MidiState
import net.merayen.elastic.backend.util.AudioUtil
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class LProcessor : LocalProcessor() {
	var type: Properties.Type? = null
	var pos = 0.0

	private val random = java.util.Random(0)

	private lateinit var frequencyCoefficients: FloatArray

	private val midiState = object : MidiState() {
		var framePosition = 0
		var lastFramePosition = 0

		override fun onKeyDown(tangent: Short, velocity: Float) {
			val newFrequency = AudioUtil.midiNoteToFreq(tangent + this.pitch)

			for (i in lastFramePosition until buffer_size - lastFramePosition)
				frequencyCoefficients[i] = newFrequency.toFloat() / sampleRate

			lastFramePosition = framePosition
		}

		override fun onKeyUp(tangent: Short) {
			for (i in lastFramePosition until buffer_size - lastFramePosition)
				frequencyCoefficients[i] = 0f

			lastFramePosition = framePosition
		}
	}

	override fun onInit() {
		frequencyCoefficients = FloatArray(buffer_size)
		for (i in 0 until buffer_size)
			frequencyCoefficients[i] = 1000f / sampleRate
	}

	override fun onPrepare() {
		midiState.lastFramePosition = 0
	}

	override fun onProcess() {
		if (frameFinished())
			return

		val out = getOutlet("out") as? SignalOutlet?: return

		val frequency = getInlet("frequency")

		if (frequency is MidiInlet) {
			for ((position, midiFrame) in frequency.outlet.midi) {
				midiState.framePosition = position
				for (midiPacket in midiFrame)
					midiState.handle(midiPacket, null)
			}
		} else if (frequency is SignalInlet) {
			for (i in 0 until buffer_size)
				frequencyCoefficients[i] = frequency.outlet.signal[i] / sampleRate
		}

		val signal = out.signal

		when (type) {
			Properties.Type.NOISE -> for (i in 0 until buffer_size) {
				val c = frequencyCoefficients[i]
				signal[i] = if (c > 0) (random.nextFloat() - 0.5f) * 0.1f else 0f //noise[(pos % noise.size).toInt()] * 0.1f
			}
			Properties.Type.SINE -> for (i in 0 until buffer_size) {
				val c = frequencyCoefficients[i]
				signal[i] = if (c > 0) sin(pos * PI * 2).toFloat() * 0.1f else 0f // Should we use a wavetable + resampler for performance? Or?
				pos += c
			}
			Properties.Type.SQUARE -> for (i in 0 until buffer_size) {
				val c = frequencyCoefficients[i]
				if (c > 0)
					signal[i] = if (pos % 2 < 1f) 0.1f else -0.1f
				else
					signal[i] = 0f
				pos += c
			}
			Properties.Type.TRIANGLE -> for (i in 0 until buffer_size) {
				val c = frequencyCoefficients[i]
				if (c > 0) {
					val p = (pos + 0.25) % 1.0
					signal[i] = ((if (p % 1 >= 0.5) p else 1.0 - p).toFloat() * 4 - 3) * 0.1f
				} else {
					signal[i] = 0f
				}
				pos += c
			}
			Properties.Type.SAW -> for (i in 0 until buffer_size) {
				val c = frequencyCoefficients[i]
				if (c > 0) {
					val p = (pos + 0.5) % 1
					signal[i] = (p * 2 - 1).toFloat() * 0.1f
				} else {
					signal[i] = 0f
				}
				pos += c
			}
			else -> for (i in 0 until buffer_size)
				signal[i] = 0f
		}

		out.push()
	}

	override fun onDestroy() {}
}