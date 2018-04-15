package net.merayen.elastic.uinodes.list.compressor_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import kotlin.math.*

class VUMeter : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var value = 0f
		set(value) {
			field = min(1f, max(0f, value))
		}

	override fun onInit() {
		super.onInit()
	}

	override fun onUpdate() {
		super.onUpdate()
		layoutHeight = layoutWidth / 1.5f
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		drawBackground(draw)

		draw.setColor(255,255,255)
		draw.setStroke(2f)
		drawArrow(draw, value, 0f, layoutHeight / 1.5f)

		draw.setColor(150,150,200)
		draw.setStroke(1f)

		var i = 0f
		while(i <= 1.01f) {
			drawArrow(draw, i, layoutWidth / 2f, layoutWidth / 1.9f)
			i += 0.1f
		}
	}

	private fun drawBackground(draw: Draw) {
		draw.setColor(10,10,10)
		draw.fillRect(0f,0f, layoutWidth, layoutHeight)
	}

	private fun drawArrow(draw: Draw, value: Float, innerRadius: Float, outerRadius: Float) {
		val radianValue = -PI / 2 - PI / 4 - value * (PI / 2)
		val xBase = layoutWidth / 2
		val yBase = layoutHeight / 1.1f
		val x1 = xBase + (sin(radianValue) * innerRadius).toFloat()
		val y1 = yBase + (cos(radianValue) * innerRadius).toFloat()
		val x2 = xBase + (sin(radianValue) * outerRadius).toFloat()
		val y2 = yBase + (cos(radianValue) * outerRadius).toFloat()

		draw.line(x1, y1, x2, y2)
	}
}