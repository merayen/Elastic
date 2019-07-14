package net.merayen.elastic.backend.logicnodes.list.midi_1

import net.merayen.elastic.system.intercom.InputFrameData

/**
 * midi_1 node send this message to the processing backend.
 * @param midiData If this is the initial frame, we send all the midi-data. If user changes anything, we resend it completely.
 * @param temporaryMidi Midi that should be played immediately. Typically input from user (plays on keyboard, pushes tangents on piano roll etc)
 */
class MidiNodeInputFrameData(nodeId: String, val midiDataMessage: MidiDataMessage? = null, val temporaryMidi: Array<ShortArray>? = null) : InputFrameData(nodeId)