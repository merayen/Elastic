package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * Midi data message
 */
data class MidiDataMessage(override val nodeId: String, val midiData: MidiData) : NodeDataMessage