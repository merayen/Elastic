package net.merayen.elastic.backend.architectures.local.nodes.midi_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet
import net.merayen.elastic.backend.data.eventdata.MidiData

class LProcessor : LocalProcessor() {
	override fun onInit() {}

	override fun onPrepare() {}

	override fun onProcess() {
		val outlet = getOutlet("out") as MidiOutlet
		if (outlet != null) {
			val inputMidi = (localNode as LNode).inputMidi
			outlet.putMidi(0, (inputMidi.midi.map { it.midi.toList().toShortArray() }.toTypedArray())); // TODO shouldn't quantize to beginning of frame
			outlet.written = buffer_size
			outlet.push()
		}
	}

	override fun onMessage(message: Any) {}

	override fun onDestroy() {}
}
