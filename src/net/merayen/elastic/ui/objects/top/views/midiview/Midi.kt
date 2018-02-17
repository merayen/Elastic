package net.merayen.elastic.ui.objects.top.views.midiview

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.midiroll.MidiRoll

class Midi : UIObject() {
	private val midi = MidiRoll(object : MidiRoll.Handler {
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
		midi.translation.scale_x = .1f
		midi.translation.scale_y = .1f
	}

	override fun onUpdate() {
		super.onUpdate()
		midi.layoutWidth = layoutWidth
		midi.layoutHeight = layoutHeight
	}
}