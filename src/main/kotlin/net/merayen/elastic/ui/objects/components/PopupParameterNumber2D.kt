package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.framework.PopupParameter
import kotlin.math.log
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A very alternative way of inputting a number.
 *
 * X-axis as logarithmic scale, and y-axis as linear scale.
 *
 * THIS IS PROBABLE A CRAP IDEA. Need to test more...
 */
class PopupParameterNumber2D : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 40f

	var minValue = 0f
	var maxValue = 1f
	private var value = 0f

	fun setValue(value: Float) {
		this.value = value
		popupParameter.y = 1f
		popupParameter.x = (value / maxValue).pow(.5f)
	}

	private val popup: UIObject = object : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.setColor(.2f, .2f, .2f, .5f)
			draw.fillRect(0f, 0f, 400f, 400f)

			draw.setColor(.2f, 1f, .2f)
			draw.rect(0f, 0f, 400f, 400f)

			draw.setColor(0f, 1f, 0f)
			draw.setFont(null, 32f)
			draw.text(
				value.toString(),
				-popupParameter.popup.translation.x,
				-popupParameter.popup.translation.y,
			)

			draw.setColor(0f, 1f, 1f)
			draw.setFont(null, 10f)
			for (y in 0 until 10) {
				for (x in 0 until 10) {
					val v = ((y / 10f) * (x / 10f).pow(2) * (maxValue - minValue) + minValue)
					val s = "%.${max(0f, 3-max(0f, log(v, 10f))).roundToInt()}f".format(v)
					draw.text(
						s,
						x * 400f / 10,
						y * 400f / 10
					)
				}
			}
		}
	}

	private val minified = object : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.setColor(0.2f, 0.2f, 0.2f)
			draw.fillRect(0f, 0f, layoutWidth, 12f)

			draw.setColor(0.7f, 0.7f, 0.7f)
			draw.rect(0f, 0f, layoutWidth, 12f)

			draw.setColor(1f, 1f, 1f)
			draw.setFont(null, 12f)
			draw.text(value.toString(), 0f, 12f)
		}
	}

	private val popupParameter = PopupParameter(minified, popup)

	override fun onInit() {
		popupParameter.setHandler(object : PopupParameter.Handler {
			override fun onGrab() {}
			override fun onMove() {
				val x = popupParameter.x
				val y = popupParameter.y
				value = y * x.pow(2) * maxValue
			}

			override fun onDrop() {
				setValue(value) // Resets Y-axis
			}
		})
		popupParameter.popup_width = 400f
		popupParameter.popup_height = 400f
		add(popupParameter)
	}
}