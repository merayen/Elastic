package net.merayen.elastic.backend.architectures.local.nodes.wave_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.backend.midi.MidiState
import net.merayen.elastic.backend.util.AudioUtil
import net.merayen.elastic.system.intercom.ElasticMessage
import kotlin.math.PI
import kotlin.math.sin

class LProcessor : LocalProcessor() {
	var type: Properties.Type? = null
	var pos = 0.0

	private lateinit var frequencyCoefficients: FloatArray

	private val midiState = object : MidiState() {
		var framePosition = 0
		var lastFramePosition = 0

		override fun onKeyDown(tangent: Short, velocity: Float) {
			val newFrequency = AudioUtil.midiNoteToFreq(tangent + this.pitch)

			for (i in lastFramePosition until buffer_size - lastFramePosition)
				frequencyCoefficients[i] = newFrequency.toFloat() / sample_rate

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
			frequencyCoefficients[i] = 1000f / sample_rate
	}

	override fun onPrepare() {
		midiState.lastFramePosition = 0
	}

	override fun onProcess() {
		val out = getOutlet("audio")

		val frequency = getInlet("frequency")

		if (out != null) {
			out as AudioOutlet
			out.channelCount = 1

			val start = out.written
			val available: Int
			if (frequency is MidiInlet) {
				available = frequency.available()
				while (true) {
					val midiFrame = frequency.getNextMidiFrame(available) ?: break
					midiState.framePosition = midiFrame.framePosition
					for (midiPacket in midiFrame)
						midiState.handle(midiPacket)
				}
				frequency.read += available
			} else {
				available = buffer_size
			}

			val factor = 440f / sample_rate // TODO take frequency from input

			val audio = out.audio[0]

			val type = type
			when (type) {
				Properties.Type.NOISE -> for (i in start until start + available) {
					val c = frequencyCoefficients[i]
					audio[i] = if (c > 0) (Math.random().toFloat() - 0.5f) * 0.1f else 0f //noise[(pos % noise.size).toInt()] * 0.1f
				}
				Properties.Type.SINE -> for (i in start until start + available) {
					val c = frequencyCoefficients[i]
					audio[i] = if (c > 0) sin(pos * PI * 2).toFloat() * 0.1f else 0f // Should we use a wavetable + resampler for performance? Or?
					pos += c
				}
				Properties.Type.SQUARE -> for (i in start until start + available) {
					val c = frequencyCoefficients[i]
					if (c > 0)
						audio[i] = if (pos % 2 < 1f) 0.1f else -0.1f
					else
						audio[i] = 0f
					pos += c
				}
				Properties.Type.TRIANGLE -> for (i in start until start + available) {
					val c = frequencyCoefficients[i]
					if (c > 0) {
						val p = (pos + 0.25) % 1.0
						audio[i] = ((if (p % 1 >= 0.5) p else 1.0 - p).toFloat() * 4 - 3) * 0.1f
					} else {
						audio[i] = 0f
					}
					pos += c
				}
				Properties.Type.SAW -> for (i in start until start + available) {
					val c = frequencyCoefficients[i]
					if (c > 0) {
						val p = (pos + 0.5) % 1
						audio[i] = (p * 2 - 1).toFloat() * 0.1f
					} else {
						audio[i] = 0f
					}
					pos += c
				}
				else -> for (i in 0 until buffer_size)
					audio[i] = 0f
			}

			out.written += available
			out.push()
		} else {
			if (frequency != null) {
				frequency.read = frequency.outlet.written
			}
		}
	}

	override fun onMessage(message: ElasticMessage) {}
	override fun onDestroy() {}
}