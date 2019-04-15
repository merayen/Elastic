package net.merayen.elastic.backend.logicnodes.list.midi_1

/**
 * Message that is to be sent to LogicNode to create a note. LogicNode will respond with a NodeParameterMessage with the updated notes.
 *
 * @param noteId Random id for the node. Must be unique
 * @param note Tangent number (pitch)
 * @param velocity How hard the tangent got pushed
 * @param length How long the tangent will be pushed in bars
 * @param eventOffset Where in the event the tangent is pushed
 * @param eventZoneId Which event zone it should be added
 */
class AddNoteMessage(val noteId: String, val note: Short, val velocity: Short, val length: Float, val eventOffset: Float, val eventZoneId: String)