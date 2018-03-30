package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class Label : UIObject() {

    var label = ""
    var font_size = 10f
    var font_name = "Geneva"
    var align: Align? = null
    var labelWidth: Float = 0.toFloat()
        private set

    enum class Align {
        LEFT, CENTER, RIGHT
    }

    override fun onDraw(draw: Draw) {
        draw.setFont(font_name, font_size)
        labelWidth = draw.getTextWidth(label)

        var x_offset = 0f
        if (align == Align.CENTER)
            x_offset = -labelWidth / 2f
        else if (align == Align.RIGHT)
            x_offset = -labelWidth

        draw.setColor(50, 50, 50)
        draw.text(label, x_offset - font_size / 10f, font_size - font_size / 10f)
        draw.setColor(200, 200, 200)
        draw.text(label, x_offset, font_size)

        super.onDraw(draw)
    }
}
