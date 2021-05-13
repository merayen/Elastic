package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.UIObject

class Label(var text: String = "", var eventTransparent: Boolean = true) : UIObject() {
	val color = MutableColor(0.8f, 0.8f, 0.8f)
	var fontSize = 10f
	var fontName = "Geneva"
	var align: Align? = null
	var labelWidth: Float = 0f
		private set
	var shadow = true

	enum class Align {
		LEFT, CENTER, RIGHT
	}

	override fun onDraw(draw: Draw) {
		if((absoluteTranslation?.scaleX ?: 0f) > fontSize / 7f) // Hide text if it is too small
			return

		if(eventTransparent)
			draw.disableOutline()

		draw.setFont(fontName, fontSize)
		labelWidth = draw.getTextWidth(text)

		var x_offset = 0f
		if (align == Align.CENTER)
			x_offset = -labelWidth / 2f
		else if (align == Align.RIGHT)
			x_offset = -labelWidth

		if (shadow) {
			draw.setColor(color.red / 2f, color.green / 2f, color.blue / 2)
			draw.text(text, x_offset - fontSize / 10f, fontSize - fontSize / 10f)
		}
		draw.setColor(color)
		draw.text(text, x_offset, fontSize)
	}

	override fun getWidth() = labelWidth
	override fun getHeight() = fontSize
}
