package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class BoxLabel : UIObject {
    var text = ""
    private var calculated_width: Float = 0f

    constructor(text: String) {
        this.text = text
    }

    override fun onDraw(draw: Draw) {
        draw.setFont("", 5f)
        draw.setStroke(1f)
        calculated_width = draw.getTextWidth(text) + 10

        draw.setColor(0, 0, 0)
        draw.text(text, 5f, 7f)
        draw.rect(0f, 0f, calculated_width, 10f)
    }

    override fun getWidth() = calculated_width
    override fun getHeight() = 10f
}
