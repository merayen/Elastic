package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.midiroll.PianoNet
import net.merayen.elastic.ui.objects.components.midiroll.PianoNotes

internal class MidiRollEventZone(val eventZoneId: String, private val midiRollEventZones: MidiRollEventZones) : UIObject(), FlexibleDimension {
	/**
	 * Gets set by us automatically. Do only set layoutHeight.
	 */
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var start = 0f
	var length = 0f
	var beatWidth = 0f

	private val notes = PianoNotes(midiRollEventZones.octaveCount)
	private lateinit var net: PianoNet

	override fun onInit() {
		net = PianoNet(midiRollEventZones.octaveCount)
		net.handler = object : PianoNet.Handler {
			override fun onRemoveMidi(id: String) {
				midiRollEventZones.handler?.onRemoveMidi(eventZoneId, id)
			}

			override fun onAddMidi(midiData: MidiData) {
				midiRollEventZones.handler?.onAddMidi(eventZoneId, midiData)
			}

			override fun onGhostNote(tangent: Short) {}
			override fun onGhostNoteOff() {}
		}
		add(net)
	}

	override fun onUpdate() {
		layoutWidth = beatWidth * length
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(1f, 0f, 1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}
}