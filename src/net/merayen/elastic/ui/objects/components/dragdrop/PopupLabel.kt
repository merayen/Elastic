package net.merayen.elastic.ui.objects.components.dragdrop

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem

abstract class PopupLabel(val text: String) : MouseCarryItem() {
	private var layoutWidth = 100f
	private var layoutHeight = 20f

	override fun onDraw(draw: Draw) {
		draw.setColor(30, 30, 30)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(100,100,100)
		draw.setStroke(2f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(200,200,200)
		draw.setFont("", 10f)
		layoutWidth = draw.getTextWidth(text) + 10f
		draw.text(text, 5f, 15f)
	}

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight
}