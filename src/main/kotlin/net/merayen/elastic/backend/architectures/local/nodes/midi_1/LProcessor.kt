package net.merayen.elastic.backend.architectures.local.nodes.midi_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet

class LProcessor : LocalProcessor() {
	override fun onInit() {}

	override fun onPrepare() {}

	override fun onProcess() {
		val outlet = getOutlet("out") as MidiOutlet?
		if (outlet != null) {
			val parent = parent as GroupLNode

			val startBeat = parent.getCursorBeatPosition()

			val stopBeat = if (parent.isPlaying()) {
				startBeat + (parent.getCurrentFrameBPM() * (buffer_size / sample_rate.toDouble())) / 60.0
			} else {
				startBeat
			}

			val lnode = (localNode as LNode)
			val inputMidi = lnode.inputMidi
			val midiData = lnode.midiData

			if (inputMidi != null) // TODO write any note from MidiData too
				outlet.putMidi(0, inputMidi); // TODO shouldn't quantize to beginning of frame

			outlet.written = buffer_size
			outlet.push()
		}
	}

	override fun onMessage(message: Any) {}

	override fun onDestroy() {}
}
