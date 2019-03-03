package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.Draw
import java.util.ArrayList

import net.merayen.elastic.ui.UIObject

internal class Menu(private val count: Int) : UIObject() {
    var radius = 150f
    var selectionRadius = 150f

    private var pointerX: Float = 0.toFloat()
    private var pointerY: Float = 0.toFloat()

    private var selected = -1

    private val items = ArrayList<ContextMenuItem>()

    override fun onDraw(draw: Draw) {
        val steps = count

        draw.setColor(150, 150, 150)

        draw.setStroke(10f)
        draw.oval(-radius, -radius, radius * 2, radius * 2)

        draw.setStroke(3f)
        draw.oval(-radius / 3, -radius / 3, radius * 2 / 3, radius * 2 / 3)

        draw.setColor(200, 200, 200)

        draw.setStroke(5f)
        draw.oval(-radius, -radius, radius * 2, radius * 2)

        draw.setStroke(1f)
        draw.oval(-selectionRadius / 3,  -selectionRadius / 3, selectionRadius * 2 / 3, selectionRadius * 2 / 3)

        drawDragLine(draw)

        val itemRadius = radius / (3 * (steps / 8.toFloat()))
        var marked = false

        selected = -1

        for (i in 0 until steps) {
            var active = false
            val x = -itemRadius + Math.sin((i / steps.toFloat()).toDouble() * Math.PI * 2.0).toFloat() * radius
            val y = -itemRadius + Math.cos((i / steps.toFloat()).toDouble() * Math.PI * 2.0).toFloat() * radius

            if (!marked && (Math.abs(pointerX) > selectionRadius / 3 || Math.abs(pointerY) > selectionRadius / 3)) {
                val pointer = ((Math.atan2((-pointerX).toDouble(), (-pointerY).toDouble()) / Math.PI + 1) / 2 * steps + 0.5f) % steps
                if (pointer.toInt() == i) {
                    marked = true
                    active = true
                }
            }

            val menuIndex = Math.floorMod(-i + count / 2, steps) // Makes items begin at 12 o'clock
            if (menuIndex < items.size) {
                val cmi = items[menuIndex]
                cmi.active = active
                cmi.radius = itemRadius
                cmi.translation.x = x
                cmi.translation.y = y

                if (active)
                    selected = menuIndex
            }
        }
    }

    private fun drawDragLine(draw: Draw) {
        draw.setColor(1.0f, 1.0f, 0f)
        draw.setStroke(5f)
        draw.line(0f, 0f, pointerX, pointerY)
        draw.fillOval(pointerX - 10f, pointerY - 10f, 20f, 20f)
    }

    fun setPointer(x: Float, y: Float) {
        pointerX = x
        pointerY = y
    }

    fun addMenuItem(item: ContextMenuItem) {
        items.add(item)
        add(item)
    }

    fun getSelected(): ContextMenuItem? {
        return if (selected > -1 && selected < items.size) items[selected] else null
    }
}
