package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import kotlin.math.max

class InlineWindow : UIObject() {
	var title = ""
	var margin = 5f
	val content = UIObject()

	override fun onInit() {
		content.translation.x = margin
		content.translation.y = margin + 20f
		add(content)
	}

	override fun onDraw(draw: Draw) {
		val width = max(50f, content.getWidth() + margin * 2)
		val height = max(50f, content.getHeight() + 20f + margin * 2)

		draw.setColor(0.1f, 0.1f, 0.1f)
		draw.fillRect(0f, 0f, width, height)

		draw.setColor(0.5f, 0.5f, 0.5f)
		draw.fillRect(0f, 0f, width, 20f)

		draw.setColor(0f, 0f, 0f)
		draw.setFont("", 10f)
		draw.text(title, margin, margin + 10f)
	}
}