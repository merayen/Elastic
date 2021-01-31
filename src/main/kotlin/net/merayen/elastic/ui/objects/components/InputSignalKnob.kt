package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class InputSignalKnob : UIObject() { // TODO delete
	var size = 30f

	private var amplitude: Knob? = null
	private var offset: Knob? = null

	override fun onInit() {
		amplitude = Knob()
		amplitude!!.value = 0.5f
		amplitude!!.size = size
		amplitude!!.dragScale = 0.05f
		add(amplitude!!)

		offset = Knob()
		offset!!.translation.x = size / 4
		offset!!.translation.y = size / 4
		offset!!.size = size / 2
		offset!!.dragScale = 0.05f
		offset!!.value = 0.5f
		add(offset!!)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(0, 0, 0)

		val amplitude_text = getFormatted(getAmplitude(), 2)
		val offset_text = getFormatted(getOffset(), 2)

		draw.setColor(80, 80, 80)
		draw.fillOval(0f, 0f, size, size)

		draw.setColor(255, 255, 255)
		draw.setFont("", size / 6)
		draw.text(amplitude_text, size / 2 - draw.getTextWidth(amplitude_text) / 2, size * 0.2f)

		draw.setFont("", size / 7)
		draw.text(offset_text, size / 2 - draw.getTextWidth(offset_text) / 2, size * 0.5f)
	}

	fun getAmplitude(): Float {
		return Math.pow((amplitude!!.value * 2).toDouble(), 8.0).toFloat()
	}

	fun getOffset(): Float {
		val v = offset!!.value
		return (Math.pow((Math.max(0.5f, v) * 2).toDouble(), 10.0) + -Math.pow((Math.max(0.5f, 1 - v) * 2).toDouble(), 10.0)).toFloat()
	}

	private fun getFormatted(number: Float, max: Int): String {
		val decimals = Math.max(0, max - Math.max(0.0, Math.log10(Math.abs(number).toDouble())).toInt())
		return String.format("%." + decimals + "f", number)
	}
}
