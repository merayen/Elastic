package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.midiroll.eventzone.MidiRollEventZones

class MidiRoll(private val handler: Handler) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onDown(tangent_no: Int)
		fun onUp(tangent_no: Int)

		fun onAddMidi(eventZoneId: String, midiData: MidiData)
		fun onChangeNote(id: String, tangent: Int, start: Float, length: Float, weight: Float)
		fun onRemoveMidi(id: String)

		fun onAddEventZone(start: Float, length: Float)
		fun onRemoveEventZone(eventZoneId: String)
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var beatWidth = 20f

	private val OCTAVE_COUNT = 8
	private lateinit var piano: Piano

	private lateinit var net: PianoNet

	private var tangentDown = -1

	private val notes = PianoNotes(OCTAVE_COUNT)

	private val eventZones = MidiRollEventZones()
	private val darkzones = EventDarkZones()

	override fun onInit() {
		net = PianoNet(OCTAVE_COUNT)
		net.handler = object : PianoNet.Handler {
			override fun onRemoveMidi(id: String) {
				TODO("not implemented")
			}

			override fun onAddMidi(midiData: MidiData) {
				val eventZoneId = eventZones.getEventZoneHit(midiData)
					?: throw RuntimeException("Could not find EventZone to place note")
				handler.onAddMidi(eventZoneId, midiData)
			}

			override fun onGhostNote(tangent: Short) {
				piano.unmarkAllTangents()
				piano.markTangent(tangent)
			}

			override fun onGhostNoteOff() {
				piano.unmarkAllTangents()
			}
		}
		add(net)

		piano = Piano(OCTAVE_COUNT, object : Piano.Handler {
			override fun onUp(tangent_no: Int) {
				handler.onUp(tangent_no)
			}

			override fun onDown(tangent_no: Int) {
				handler.onDown(tangent_no)
			}
		})

		darkzones.handler = object : EventDarkZones.Handler {
			override fun onCreateEventZone(start: Float) {
				// TODO make sure we do not overlap anything
				handler.onAddEventZone(start, 4f) // TODO figure out length
			}
		}

		add(notes)
		add(darkzones)
		add(piano)
	}

	override fun onUpdate() {
		// Hardcoded for noe
		net.beatWidth = beatWidth
		darkzones.beatWidth = beatWidth

		net.layoutWidth = layoutWidth

		notes.layoutWidth = layoutWidth
		notes.layoutHeight = layoutHeight

		net.translation.x = piano.pianoDepth
		net.layoutWidth = layoutWidth - piano.pianoDepth

		if (tangentDown != -1) {
			handler.onUp(tangentDown)
			tangentDown = -1
		}

		darkzones.translation.x = piano.pianoDepth
		darkzones.layoutHeight = net.layoutHeight
	}

	private fun getDarkZoneStartStops(): ArrayList<StartStop> {
		val startStops = eventZones.getStartStops()

		if (startStops.isEmpty()) {
			val result = ArrayList<StartStop>()
			result.add(StartStop(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY))
			return result
		}

		// Invert startStops
		val result = ArrayList<StartStop>()

		// Add beginning
		result.add(StartStop(-1000000f, 1000000 + startStops[0].start))

		// Add inverts in between the event zones
		for (i in 0 until startStops.size - 1)
			result.add(StartStop(startStops[i].start + startStops[i].length, startStops[i + 1].start))

		// Add end
		result.add(StartStop(
			startStops[startStops.size - 1].start + startStops[startStops.size - 1].length,
			1000000 - (startStops[startStops.size - 1].start + startStops[startStops.size - 1].length))
		)

		return result
	}

	fun handleMessage(message: NodeParameterMessage) {
		eventZones.handleMessage(message)

		// Update dark zones
		darkzones.applyDarkZones(getDarkZoneStartStops()) // TODO only when necessary
	}

	override fun getWidth() = net.getWidth()
	override fun getHeight() = net.getHeight()
}
