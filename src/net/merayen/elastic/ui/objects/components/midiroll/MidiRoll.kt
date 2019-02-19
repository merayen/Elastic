package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class MidiRoll(private val handler: Handler) : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private lateinit var piano: Piano
	private lateinit var net: PianoNet
	private lateinit var midiZones: MidiZones
	private lateinit var notes: PianoNotes

	private val OCTAVE_COUNT = 8

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
