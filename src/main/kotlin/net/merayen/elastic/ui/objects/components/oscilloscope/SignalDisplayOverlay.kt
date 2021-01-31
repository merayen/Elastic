package net.merayen.elastic.ui.objects.components.oscilloscope

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import kotlin.math.roundToInt

class SignalDisplayOverlay : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var time = 0f

	override fun onDraw(draw: Draw) {
		draw.setColor(0.8f, 0.8f, 0f)
		draw.setFont("", 9f)
		val text = "${(time * 1000).roundToInt()}ms"
		val width = draw.getTextWidth(text)
		draw.text(text, layoutWidth - width, layoutHeight - 5f)
	}
}