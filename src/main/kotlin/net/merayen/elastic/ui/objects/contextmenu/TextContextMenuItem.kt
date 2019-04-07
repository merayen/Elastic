package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.Draw

/**
 * Only shows a label.
 */
class TextContextMenuItem(var text: String) : ContextMenuItem() {
    override fun onDraw(draw: Draw) {
        super.onDraw(draw)

        val radius = radius

        draw.setFont("", 10f)
        var width = draw.getTextWidth(text)
        val font_size = radius * 2f / (width / 8)
        draw.setFont("", font_size)
        width = draw.getTextWidth(text)

        draw.setColor(255, 255, 255)

        draw.text(text, -width / 2 + radius, radius + font_size / 2.5f)
    }
}
