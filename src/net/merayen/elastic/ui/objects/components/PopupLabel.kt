package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import kotlin.math.sin

class PopupLabel(var text: String = "") : UIObject() {
	private var layoutWidth = 100f
	private var layoutHeight = 20f

	var fontSize = 10f

	override fun onDraw(draw: Draw) {
		draw.setFont("", fontSize)
		layoutWidth = draw.getTextWidth(text) + 10f
		layoutHeight = fontSize + 10f + fontSize / 5f

		draw.setColor(30, 30, 30)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(100,100,100)
		draw.setStroke(2f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(200,200,200)
		draw.text(text, 5f, 5f + fontSize)
	}

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight
}