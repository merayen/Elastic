package net.merayen.elastic.backend.architectures.local.nodes.from_midi_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalOutlet
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.SessionKeeper
import net.merayen.elastic.backend.midi.MidiState
import net.merayen.elastic.backend.util.AudioUtil
import java.util.*

class LProcessor : LocalProcessor(), SessionKeeper {
	private var activeTangent: ShortArray? = null
	private val keysDown: ArrayList<ShortArray> = ArrayList()
	private var pos = Random().nextDouble() * Math.PI * 2
	private var volume = 0f

	private var done = false

	private val midiState: MidiState = object : MidiState() {
		override fun onPitchBendSensitivityChange(semitones: Float) {}

		override fun onKeyDown(tangent: Short, velocity: Float) {
			if (sustain == 0f) keysDown.add(currentMidiPacket) // TODO verify the sustain-comparison
			activeTangent = currentMidiPacket
		}

		override fun onKeyUp(tangent: Short) {
			keysDown.removeIf { it[1] == tangent }
			// TODO verify the sustain-comparison
			if (sustain == 0f)
				activeTangent = if (keysDown.isEmpty()) null else keysDown[keysDown.size - 1]
		}

		override fun onSustain(value: Float) {
			/*if (sustain && getCurrentMidiPacket()[2] == 0) {
				if (keys_down.isEmpty())
					active_tangent = null;
				else
					active_tangent = keys_down.get(keys_down.size() - 1);
			}

			sustain = getCurrentMidiPacket()[2] != 0;*/
		}

		override fun onVolumeChange(volume: Float) {}

		override fun onPitchChange(semitones: Float) {
			//System.out.println(getSessionID() + "\t" + semitones);
		}
	}

	override fun onInit() {}

	override fun onPrepare() {
		done = false
	}

	override fun onProcess() {
		if (done || !available())
			return

		val inlet = getInlet("in")

		if (inlet is MidiInlet) {
			var lastPosition = 0
			for ((midiPosition, midiFrame) in inlet.outlet.midi) {
				renderOutSignal(lastPosition, midiPosition)

				// Handle all the midi data on this exact time
				for (midiPacket in midiFrame)
					midiState.handle(midiPacket)

				lastPosition = midiPosition
			}

			// Render any remaining packets
			renderOutSignal(lastPosition, buffer_size)
		}

		getOutlet("frequency")?.push()
		getOutlet("amplitude")?.push()
		getOutlet("velocity")?.push()

		done = true
	}

	override fun onDestroy() {}

	override fun isKeepingSessionAlive() = activeTangent != null

	private fun renderOutSignal(from: Int, to: Int) {
		val frequency = (getOutlet("frequency") as? SignalOutlet)?.signal
		val amplitude = (getOutlet("amplitude") as? SignalOutlet)?.signal

		val activeTangent = activeTangent

		if (activeTangent != null) {
			// TODO Interpolate between midiframes too?
			if (frequency != null) {
				val freq = AudioUtil.midiNoteToFreq(activeTangent[1] + midiState.pitch).toFloat()
				for (i in from until to)
					frequency[i] = freq
			}

			if (amplitude != null) {
				val velocity = midiState.velocity
				val volume = midiState.volume
				for (i in from until to)
					amplitude[i] = velocity * volume
			}
		} else { // No keys down. Output silence
			if (frequency != null)
				for (i in from until to)
					frequency[i] = 0f

			if (amplitude != null)
				for (i in from until to)
					amplitude[i] = 0f
		}
	}
}