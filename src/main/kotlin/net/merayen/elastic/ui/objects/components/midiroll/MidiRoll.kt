package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.SelectionRectangle

class MidiRoll(private val handler: Handler) : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val OCTAVE_COUNT = 8
	private lateinit var piano: Piano
	private lateinit var net: PianoNet

	private val notes = PianoNotes(OCTAVE_COUNT)

	private val selectionReadable = SelectionRectangle()

	interface Handler {
		fun onDown(tangent_no: Int)
		fun onUp(tangent_no: Int)
	}

	override fun onInit() {
		net = PianoNet(OCTAVE_COUNT)
		add(net)

		piano = Piano(OCTAVE_COUNT, object : Piano.Handler {
			override fun onUp(tangent_no: Int) {
				handler.onUp(tangent_no)
			}

			override fun onDown(tangent_no: Int) {
				handler.onDown(tangent_no)
			}
		})

		add(piano)
	}

	override fun onUpdate() {
		net.layoutWidth = layoutWidth
	}

	fun loadMidi(midi: Array<Array<Short>>) {

	}

	fun retrieveMidi(): Array<Array<Short>> {
		TODO()
	}

	override fun getWidth() = net.getWidth()
	override fun getHeight() = net.getHeight()
}
