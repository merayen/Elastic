package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.list.midi_1.AddEventZoneMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.ChangeEventZoneMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.RemoveEventZoneMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
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

	fun handleMessage(message: NodeDataMessage) {
		when (message) {
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
		}

		eventZones.sortBy { it.start }
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

		// Validates that there is just 1 position of the midi packets and that there is at least 1 midi packet
		val starts = midiChunks.map { it.start }.toSet()
		if (starts.size != 1)
			throw RuntimeException("Expected added midi to have at least 1 midi packet and only 1 start point")

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