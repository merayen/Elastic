package net.merayen.elastic.ui.objects.top.views.midiview

import net.merayen.elastic.ui.objects.top.views.View

class MidiView : View() {
	private val bar = MidiViewBar()
	private val midi = Midi()

	override fun cloneView(): View {
		return MidiView()
	}

	override fun onInit() {
		super.onInit()
		add(midi)
		add(bar)

		midi.translation.y = 20f
	}

	override fun onDraw() {
		super.onDraw()
		draw.setColor(50,50,100)
		draw.fillRect(30f,30f,50f,50f)
	}

	override fun onUpdate() {
		super.onUpdate()
		midi.layoutWidth = width
		midi.layoutHeight = height - 30
	}
}