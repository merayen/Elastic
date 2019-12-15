package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.midiroll.PianoNet
import net.merayen.elastic.ui.objects.components.midiroll.PianoNotes

internal class MidiRollEventZone(val eventZoneId: String, private val midiRollEventZones: MidiRollEventZones) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onResize(offsetPosition: Float, offsetLength: Float)
	}

	/**
	 * Gets set by us automatically. Do only set layoutHeight.
	 */
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var handler: Handler? = null

	var start = 0f
	var length = 0f
	var beatWidth = 0f

	private val notes = PianoNotes(midiRollEventZones.octaveCount)
	private val bar = HorizontalResizableBox()
	private lateinit var net: PianoNet

	override fun onInit() {
		bar.layoutHeight = 10f
		bar.handler = object : HorizontalResizableBox.Handler {
			private var originalStart = 0f
			private var originalLength = 0f

			override fun onChange(offsetPosition: Float, offsetLength: Float) {
				handler?.onResize(offsetPosition / beatWidth, offsetLength / beatWidth)
			}
		}
		add(bar)

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
		net.translation.y = 10f
		add(net)
	}

	override fun onUpdate() {
		layoutWidth = beatWidth * length
		translation.x = beatWidth * start
		bar.layoutWidth = layoutWidth
		net.beatWidth = beatWidth
		net.beatCount = length
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(1f, 0f, 1f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
	}
}