package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.list.midi_1.ChangeEventZoneMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class MidiRollEventZones(val octaveCount: Int) : UIObject() {
	interface Handler {
		/**
		 * Called if user changes the start or stop position of the event zone
		 */
		fun onChange(message: ChangeEventZoneMessage)

		fun onAddMidi(eventZoneId: String, midiData: MidiData)
		fun onRemoveMidi(eventZoneId: String, id: String)

		fun onGhostNote(tangent: Short)
		fun onGhostNoteOff(tangent: Short)
	}

	var layoutHeight = 0f

	var handler: Handler? = null

	private val eventZones = ArrayList<MidiRollEventZone>()

	fun handleMessage(message: NodePropertyMessage) {
		val data = message.instance as Properties

		val newEventZones = data.eventZones

		if (newEventZones != null) {
			eventZones.forEach { remove(it) }
			eventZones.clear()

			for (newZone in newEventZones) {
				val m = MidiRollEventZone(newZone.id!!, this)
				m.start = newZone.start!!
				m.length = newZone.length!!

				eventZones.add(m)
				add(m)
			}

			eventZones.sortBy { it.start }
		}
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0f, 0f, 0f)
		draw.fillRect(0f, 0f, 100f, layoutHeight)
	}

	override fun onUpdate() {
		for (zone in eventZones)
			zone.layoutHeight = layoutHeight
	}
}