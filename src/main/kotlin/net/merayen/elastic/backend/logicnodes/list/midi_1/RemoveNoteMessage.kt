package net.merayen.elastic.backend.logicnodes.list.midi_1

/**
 * Remove a note in an event zone.
 */
class RemoveNoteMessage(val noteId: String, val eventZoneId: String)