package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.UIObject

internal class Piano(private val octave_count: Int, private val handler: Handler) : UIObject() {

	var pianoDepth = 20f
	var octave_width = (5 * 7).toFloat()
	var spacing = 0.2f

	private val tangents = HashMap<Short, Tangent>()
	private val WHITE_POSITIONS = intArrayOf(0, 2, 4, 5, 7, 9, 11)
	private val BLACK_POSITIONS = intArrayOf(1, 3, 6, 8, 10)

	interface Handler {
		fun onDown(tangent_no: Int)
		fun onUp(tangent_no: Int)
	}

	override fun onInit() {
		var y = 0f

		for (i in 0 until octave_count * 7) {
			val t = Tangent(false, getTangentHandler(WHITE_POSITIONS[i % 7] + 12 * (i / 7)))
			t.translation.x = spacing
			t.translation.y = octave_width * octave_count - (y + spacing + octave_width / 7)
			t.width = pianoDepth - spacing * 2
			t.height = octave_width / 7 - spacing * 2
			tangents[(WHITE_POSITIONS[i % WHITE_POSITIONS.size] + (i / WHITE_POSITIONS.size) * 12).toShort()] = t
			add(t)

			y += octave_width / 7
		}

		y = 0f
		var pos = 0
		for (i in 0 until octave_count * 7) {
			if (pos != 2 && pos != 6) {
				val t = Tangent(true, getTangentHandler(WHITE_POSITIONS[i % 7] + 1 + 12 * (i / 7)))
				t.translation.x = spacing
				t.translation.y = octave_width * octave_count - (y + spacing + octave_width / 7) - octave_width / (7 * 3)
				t.width = pianoDepth / 2
				t.height = octave_width / 7 / 1.5f - spacing * 2
				tangents[(BLACK_POSITIONS[i % BLACK_POSITIONS.size] + (i / 7) * 12).toShort()] = t
				add(t)
			}

			y += octave_width / 7
			pos++
			pos %= 7
		}
	}

	private fun getTangentHandler(tangent_no: Int): Tangent.Handler {
		return object : Tangent.Handler {
			override fun onDown() {
				for (t in tangents.values)
					t.goStandby()

				handler.onDown(tangent_no + 12 * 2)
			}

			override fun onUp() {
				handler.onUp(tangent_no + 12 * 2)
			}
		}
	}

	fun markTangent(tangent: Short, mark: Boolean = true) {
		tangents.get(tangent)?.marked = mark
	}

	fun unmarkAllTangents() {
		for (tangent in tangents.values)
			tangent.marked = false
	}
}
