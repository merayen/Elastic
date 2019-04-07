package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.UIObject

class PianoNotes : UIObject() {
	private lateinit var midiZones: MidiZones

	override fun onInit() {
		midiZones = MidiZones()
	}
}
