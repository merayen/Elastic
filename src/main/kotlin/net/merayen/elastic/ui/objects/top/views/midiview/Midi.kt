package net.merayen.elastic.ui.objects.top.views.midiview

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.midiroll.MidiRoll

class Midi : UIObject() {
	private val midi = MidiRoll(object : MidiRoll.Handler {
		override fun onAddMidi(midiData: MidiData) {

		}

		override fun onChangeNote(id: String, tangent: Int, start: Float, length: Float, weight: Float) {

		}

		override fun onRemoveMidi(id: String) {

		}

		override fun onDown(tangent_no: Int) {

		}

		override fun onUp(tangent_no: Int) {

		}
	})

	var layoutWidth = 0f
	var layoutHeight = 0f

	override fun onInit() {
		super.onInit()
		add(midi)
		midi.translation.scaleX = .1f
		midi.translation.scaleY = .1f
	}

	override fun onUpdate() {
		midi.layoutWidth = layoutWidth
		midi.layoutHeight = layoutHeight
	}
}