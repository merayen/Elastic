package net.merayen.elastic.backend.logicnodes.list.midi_in_1

import net.merayen.elastic.system.intercom.InputFrameData

class MidiIn1InputFrameData(nodeId: String, val midi: Array<ShortArray>? = null) : InputFrameData(nodeId)