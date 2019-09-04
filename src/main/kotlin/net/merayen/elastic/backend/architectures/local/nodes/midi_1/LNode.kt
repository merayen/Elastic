package net.merayen.elastic.backend.architectures.local.nodes.midi_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.list.midi_1.MidiNodeInputFrameData
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	internal var inputMidi: Array<ShortArray>? = null
	internal var midiData: MidiData? = null

	override fun onInit() {}

	override fun onSpawnProcessor(lp: LocalProcessor) {}

	override fun onProcess(data: InputFrameData) {
		val input = data as MidiNodeInputFrameData
		if (input.midiDataMessage != null)
			midiData = input.midiDataMessage.midiData

		if (input.temporaryMidi != null)
			inputMidi = input.temporaryMidi
	}

	override fun onParameter(instance: BaseNodeProperties) {}

	override fun onFinishFrame() {
		inputMidi = null
	}

	override fun onDestroy() {}
}
