package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.system.intercom.NodeDataMessage

class AddMidiMessage(
		override val nodeId: String,
		val midiData: MidiData
) : NodeDataMessage