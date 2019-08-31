package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.list.midi_1.ChangeEventZoneMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.Data
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.midiroll.StartStop

class MidiRollEventZones : UIObject(), FlexibleDimension {
	interface Handler {
		/**
		 * Called if user changes the start or stop position of the event zone
		 */
		fun onChange(message: ChangeEventZoneMessage)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private val eventZones = ArrayList<MidiRollEventZone>()

	override fun onDraw(draw: Draw) {

	}

	fun handleMessage(message: NodeParameterMessage) {
		val data = message.instance as Data
		/*when (message) {
			is AddEventZoneMessage -> {
				val zone = MidiRollEventZone(message.eventZoneId)
				eventZones.add(zone)
				add(zone)
			}
			is ChangeEventZoneMessage -> {
				val zone = eventZones.first { it.id == message.eventZoneId }
				zone.start = message.start
				zone.length = message.length
			}
			is RemoveEventZoneMessage -> {
				val zone = eventZones.first { it.id == message.eventZoneId }
				remove(zone)
				eventZones.remove(zone)
			}
			else -> return
		}*/

		val newEventZones = data.eventZones

		if (newEventZones != null) {
			eventZones.forEach { remove(it) }
			eventZones.clear()

			for (newZone in newEventZones) {
				val m = MidiRollEventZone(newZone.id!!)
				m.start = newZone.start!!
				m.length = newZone.length!!

				eventZones.add(m)
				add(m)
			}

			eventZones.sortBy { it.start }
		}
	}

	override fun onUpdate() {
		for (zone in eventZones) {
			zone.layoutHeight = layoutHeight
		}
	}

	/**
	 * Returns the event zone id that the midiData-packet hits (where its start position is placed.
	 * Returns null if none is found.
	 */
	fun getEventZoneHit(midiData: MidiData): String? {
		val midiChunks = midiData.getMidiChunks()

		if (midiChunks.size == 0)
			throw RuntimeException("Expected added midi to have at least 1 midi packet")

		val point = midiChunks[0].start

		val result = eventZones.filter { it.start > point && it.start + it.length > point}

		return if (result.isEmpty()) null else result[0].id
	}

	/**
	 * Return event zones in this format {<event zone 1 start>, <event zone 1 length>, <event zone 2 start}, ...}
	 */
	fun getStartStops() = eventZones.map {
		StartStop(it.start, it.length)
	}
}