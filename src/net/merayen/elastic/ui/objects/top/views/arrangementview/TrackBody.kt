package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

internal class TrackBody : UIObject() {
    var layoutWidth = 0f
    var layoutHeight = 0f

    override fun onDraw(draw: Draw) {
        draw.setColor(20, 20, 50)
        draw.fillRect(2f, 2f, layoutWidth - 4, layoutHeight - 4)
    }
}
