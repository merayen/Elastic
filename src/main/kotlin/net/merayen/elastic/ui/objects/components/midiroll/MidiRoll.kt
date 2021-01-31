package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.midiroll.eventzone.MidiRollEventZones

class MidiRoll(private val handler: Handler) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onDown(tangent_no: Int)
		fun onUp(tangent_no: Int)

		fun onAddMidi(eventZoneId: String, midiData: MidiData)
		fun onChangeNote(id: String, tangent: Int, start: Float, length: Float, weight: Float)
		fun onRemoveMidi(eventZoneId: String, id: String)

		fun onCreateEventZone(start: Float, length: Float)
		fun onChangeEventZone(eventZoneId: String, start: Float, length: Float)
		fun onRemoveEventZone(eventZoneId: String)

		fun onPlayheadMoved(beat: Float)
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var beatWidth = 20f // Horizontal zoom, in other words

	private val OCTAVE_COUNT = 8
	private lateinit var piano: Piano

	private var tangentDown = -1

	private val eventZones = MidiRollEventZones(OCTAVE_COUNT)
	private val scroll = Scroll(eventZones)

	override fun onInit() {
		scroll.translation.x = 20f
		eventZones.handler = object : MidiRollEventZones.Handler {
			override fun onCreateEventZone(start: Float, length: Float) = handler.onCreateEventZone(start, length)
			override fun onChangeEventZone(eventZoneId: String, start: Float, length: Float) = handler.onChangeEventZone(eventZoneId, start, length)
			override fun onAddMidi(eventZoneId: String, midiData: MidiData) = handler.onAddMidi(eventZoneId, midiData)
			override fun onRemoveMidi(eventZoneId: String, id: String) = handler.onRemoveMidi(eventZoneId, id)

			override fun onGhostNote(tangent: Short) {
				piano.unmarkAllTangents()
				piano.markTangent(tangent)
			}

			override fun onGhostNoteOff(tangent: Short) {
				piano.unmarkAllTangents()
			}

			override fun onPlayheadMoved(beat: Float) {
				handler?.onPlayheadMoved(beat)
			}
		}
		piano = Piano(OCTAVE_COUNT, object : Piano.Handler {
			override fun onUp(tangent_no: Int) {
				handler.onUp(tangent_no)
			}

			override fun onDown(tangent_no: Int) {
				handler.onDown(tangent_no)
			}
		})
		add(scroll)
		add(piano)
	}

	override fun onUpdate() {
		scroll.layoutWidth = layoutWidth
		scroll.layoutHeight = layoutHeight
		eventZones.layoutHeight = layoutHeight
		eventZones.beatWidth = beatWidth

		if (tangentDown != -1) {
			handler.onUp(tangentDown)
			tangentDown = -1
		}
	}

	fun handleMessage(message: NodeMessage) {
		eventZones.handleMessage(message)
	}
}
