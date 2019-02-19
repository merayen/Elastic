package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.util.Movable

class Scroll(private val uiobject: UIObject) : UIObject() {
    var layoutWidth = 100f
    var layoutHeight = 100f
    var barWidth = 10f

    private val clip = UIClip()
    private val barX = Bar(true)
    private val barY = Bar(false)

    private var contentWidth = 0f
    private var contentHeight = 0f
    private var moving = false

    private inner class Bar constructor(private val x: Boolean) : UIObject() {
        private val movable = Movable(this, this)

        override fun onInit() {
            if (x)
                movable.drag_scale_y = 0f
            else
                movable.drag_scale_x = 0f

            movable.setHandler(object : Movable.IMoveable {
                override fun onMove() {
                    updateFromBars()
                }

                override fun onGrab() {
                    moving = true
                }
                override fun onDrop() {
                    moving = false
                }
            })
        }

        override fun onDraw(draw: Draw) {
            draw.setColor(255, 0, 255)
            draw.fillRect(0f, 0f, barWidth, barWidth)
        }

        override fun onEvent(event: UIEvent) {
            movable.handle(event)
        }
    }

    override fun onInit() {
        clip.add(uiobject)
        add(clip)
        updateBars()
    }

    override fun onDraw(draw: Draw) {
        draw.setColor(50, 50, 50)
        draw.fillRect(0f, layoutHeight - barWidth, layoutWidth, barWidth)
        draw.fillRect(layoutWidth - barWidth, 0f, barWidth, layoutHeight)
    }

    override fun onUpdate() {
        contentWidth = uiobject.getWidth()
        contentHeight = uiobject.getHeight()

        clip.width = layoutWidth - barWidth
        clip.height = layoutHeight - barWidth

        if (uiobject.translation.x > 0f)
            uiobject.translation.x = 0f

        if (uiobject.translation.y > 0f)
            uiobject.translation.y = 0f

        if (uiobject.translation.x < -(contentWidth - layoutWidth) && contentWidth > layoutWidth)
            uiobject.translation.x = -(contentWidth - layoutWidth)

        if (uiobject.translation.y < -(contentHeight - layoutHeight) && contentHeight > layoutHeight)
            uiobject.translation.y = -(contentHeight - layoutHeight)

        barX.translation.y = layoutHeight - barWidth
        barY.translation.x = layoutWidth - barWidth

        if(!moving)
            updateBars()
    }

    private fun updateBars() {
        if (contentWidth - layoutWidth > 0) {
            if (barX.parent == null)
                add(barX)

            barX.translation.y = layoutHeight - barWidth
        } else if (barX.parent != null) {
            remove(barX)
        }

        if (contentHeight - layoutHeight > 0) {
            if (barY.parent == null)
                add(barY)

            barY.translation.x = layoutWidth - barWidth
        } else if (barY.parent != null) {
            remove(barY)
        }

        barX.translation.x = Math.max(0f, Math.min(layoutWidth - barWidth, barX.translation.x))
        barY.translation.y = Math.max(0f, Math.min(layoutHeight - barWidth, barY.translation.y))
    }

    private fun updateFromBars() {
        uiobject.translation.x = barX.translation.x / (layoutWidth - barWidth) * -(contentWidth - layoutWidth)
        uiobject.translation.y = barY.translation.y / (layoutHeight - barWidth) * -(contentHeight - layoutHeight)
    }
}
