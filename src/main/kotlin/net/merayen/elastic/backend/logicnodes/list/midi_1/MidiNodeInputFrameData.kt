package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.InputFrameData

class MidiNodeInputFrameData(nodeId: String, val midiDataMessage: MidiDataMessage) : InputFrameData(nodeId)