package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.objects.components.Button
import net.merayen.elastic.ui.objects.components.Label

class MidiTrack(nodeId: String, arrangement: Arrangement) : ArrangementTrack(nodeId, arrangement) {
	private var mute = false

	init {
		val muteButton = Button("M")
		muteButton.handler = object : Button.IHandler {
			override fun onClick() {
				sendParameter("mute", true)
			}
		}
		trackPane.buttons.add(muteButton)

		eventPane.add(Label("Hello on you! I am a midi track!"))
	}

	override fun onParameter(key: String, value: Any) {
		if (key == "mute")
			mute = value as Boolean
	}
}