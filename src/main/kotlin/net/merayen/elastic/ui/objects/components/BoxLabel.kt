package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.UIObject

class BoxLabel(var text: String = "") : UIObject() {
    private var calculatedWidth: Float = 0f

    val color = MutableColor()

    override fun onDraw(draw: Draw) {
        draw.setFont("", 10f)
        draw.setStroke(1f)
        calculatedWidth = draw.getTextWidth(text) + 10

        draw.setColor(0, 0, 0)
        draw.text(text, 5f, 10f)
        draw.rect(0f, 0f, calculatedWidth, 12f)
    }

    override fun getWidth() = calculatedWidth
    override fun getHeight() = 10f
}
