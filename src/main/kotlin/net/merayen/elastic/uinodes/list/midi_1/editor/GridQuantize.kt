package net.merayen.elastic.uinodes.list.midi_1.editor

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.PopupParameter1D
import kotlin.math.pow
import kotlin.math.roundToInt

class GridQuantize : UIObject() {
	var quantizeScale = PopupParameter1D()
	private var division = 1

	override fun onInit() {
		quantizeScale.handler = object : PopupParameter1D.Handler {
			override fun onMove(value: Float) {
				division = 2.0.pow((value * 6).roundToInt()).toInt()
			}

			override fun onChange(value: Float) {}

			override fun onLabel(value: Float) = "Grid 1/${2.0.pow((value * 6).roundToInt()).toInt()}"
		}

		add(quantizeScale)
	}
}