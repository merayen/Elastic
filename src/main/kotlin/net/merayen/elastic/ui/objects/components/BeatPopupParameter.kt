package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.UIObject
import kotlin.math.pow
import kotlin.math.roundToInt

class BeatPopupParameter : UIObject() {
	interface Handler {
		fun onLabel(value: Int): String
		fun onChange(value: Int)
	}

	var handler: Handler? = null

	private val popupParameter = PopupParameter1D()
	var value = 0
	set (value) {
		field = value
		popupParameter.label.text = handler?.onLabel(value) ?: ""
	}

	var maxValue = 4 * 20

	override fun onInit() {
		popupParameter.drag_scale = 0.5f
		popupParameter.handler = object : PopupParameter1D.Handler {
			override fun onChange(value: Float) {
				this@BeatPopupParameter.value = (value.pow(2) * maxValue).roundToInt()
				handler?.onChange(this@BeatPopupParameter.value)
			}

			override fun onMove(value: Float) {
				this@BeatPopupParameter.value = (value.pow(2) * maxValue).roundToInt()
			}

			override fun onLabel(value: Float) = handler?.onLabel((value.pow(2) * maxValue).roundToInt()) ?: ""
		}

		add(popupParameter)
	}
}