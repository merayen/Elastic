package net.merayen.elastic.backend.architectures.local.nodes.metronome_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet
import net.merayen.elastic.backend.midi.MidiMessagesCreator
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

class LProcessor : LocalProcessor() {
	private var lastBeatPosition = 0

	private var audioBeepPosition = Int.MAX_VALUE
	private var midiBeepPosition = Int.MAX_VALUE

	private var midiState = 0

	override fun onInit() {}
	override fun onPrepare() {}
	override fun onProcess() {
		val audio = getOutlet("audio") as? AudioOutlet
		val midi = getOutlet("midi") as? MidiOutlet

		val parent = (localNode.parent as GroupLNode)
		val currentBeatPosition = parent.getBeatPosition()
		val playing = parent.isPlaying()

		if (playing && currentBeatPosition.toInt() != lastBeatPosition) {
			audioBeepPosition = 0
			midiBeepPosition = 0
			lastBeatPosition = currentBeatPosition.toInt()
		}

		val beepLength = sampleRate / 10f

		if (audio != null) {
			if (localNode.parentGroupNode.getChannelCount() > 1)
				audio.audio[1] = audio.audio[0]

			val hz = if (lastBeatPosition == 0) 1760 else 880

			if (audioBeepPosition < beepLength)
				for (i in 0 until buffer_size)
					audio.audio[0][i] = sin(audioBeepPosition++ / sampleRate.toDouble() * PI * 2 * hz).toFloat() * (1 - audioBeepPosition / beepLength).pow(2)
			else
				for (i in 0 until buffer_size)
					audio.audio[0][i] = 0f

			audio.push()
		}

		if (midi != null) {
			if (midiState == 0 && midiBeepPosition == 0) {
				midi.addMidi(0, MidiMessagesCreator.keyDown(if(lastBeatPosition == 0) 69+12 else 69, 1f))
				midiState++
			} else if (midiState == 1 && midiBeepPosition > beepLength) {
				midi.addMidi(0, MidiMessagesCreator.keyUp(69, 0f))
				midi.addMidi(0, MidiMessagesCreator.keyUp(69+12, 0f))
				midiState = 0
			}

			midiBeepPosition += buffer_size

			midi.push()
		}
	}

	override fun onDestroy() {}
}