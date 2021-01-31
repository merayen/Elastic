package net.merayen.elastic.backend.architectures.local.nodes.midi_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet

class LProcessor : LocalProcessor() {
	private var playStartedCount: Long = 0
	private var midiDataRevision = 0L

	override fun onInit() {}

	override fun onPrepare() {}

	override fun onProcess() {
		val outlet = getOutlet("out") as MidiOutlet?
		if (outlet != null) {
			val parent = localNode.parent as GroupLNode

			val startBeat = parent.getCursorPosition()

			val newPlayStartedCount = parent.playStartedCount()
			if (newPlayStartedCount != playStartedCount)
				playStartedCount = newPlayStartedCount

			val stopBeat = if (parent.isPlaying()) {
				startBeat + (parent.getCurrentFrameBPM() * (buffer_size / sampleRate.toDouble())) / 60.0
			} else {
				startBeat
			}

			if (parent.isPlaying())
				; // TODO println("Playing! startBeat=${startBeat}, stopBeat=${stopBeat}")

			val lnode = (localNode as LNode)
			val inputMidi = lnode.inputMidi
			val midiData = lnode.midiData

			if (inputMidi != null) // TODO write any note from MidiData too
				outlet.addMidi(0, inputMidi) // TODO shouldn't quantize to beginning of frame

			if (midiData != null) {
				if (midiData.revision != midiDataRevision) {
					println("Midi data received!")
					midiDataRevision = midiData.revision
				}
			}

			outlet.push()
		}
	}

	override fun onDestroy() {}
}
