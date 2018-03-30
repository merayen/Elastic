package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

abstract class ContextMenuItem : UIObject() {
    var active: Boolean = false // true if being selected
    var radius: Float = 0.toFloat()
    internal var x: Float = 0.toFloat()
    internal var y: Float = 0.toFloat()

    override fun onDraw(draw: Draw) {
        if (active)
            draw.setColor(100, 150, 100)
        else
            draw.setColor(100, 100, 100)

        draw.fillOval(0f, 0f, radius * 2, radius * 2)

        if (active)
            draw.setColor(200, 255, 200)
        else
            draw.setColor(50, 50, 50)

        draw.setStroke(radius / 10)
        draw.oval(0f, 0f, radius * 2, radius * 2)
    }
}
