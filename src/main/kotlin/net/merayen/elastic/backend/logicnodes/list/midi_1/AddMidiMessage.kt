package net.merayen.elastic.backend.logicnodes.list.midi_1

/**
 * Message that is to be sent to LogicNode to create a midi event. LogicNode will respond with a NodeParameterMessage with the updated notes.
 *
 * @param id Random id for the node. Must be unique
 * @param midi The midi packet
 * @param offset Where in the event the tangent is pushed
 * @param eventZoneId Which event zone it should be added
 */
class AddMidiMessage(val id: String, val midi: Array<Short>, val offset: Float, val eventZoneId: String)