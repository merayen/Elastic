package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.ui.util.UINodeUtil
import net.merayen.elastic.util.Point

/**
 * Puts up a context menu on top of everything (Top().overlay)
 */
class ContextMenu(trigger: UIObject, count: Int, button: MouseEvent.Button, handler: Handler) {

    private val menu: Menu
    private val mouse: MouseHandler

    interface Handler {
        fun onSelect(item: ContextMenuItem?, position: Point)
    }

    constructor(trigger: UIObject, button: MouseEvent.Button, handler: Handler) : this(trigger, 8, button, handler) {}

    init {
        menu = Menu(count)

        mouse = MouseHandler(trigger, button)
        mouse.setHandler(object : MouseHandler.Handler() {
            var start_x: Float = 0.toFloat()
            var start_y: Float = 0.toFloat()
            var relative: Point? = null

            override fun onMouseDown(position: Point) {
                UINodeUtil.getWindow(trigger).overlay.add(menu)
                val absolute = trigger.getAbsolutePosition(position.x, position.y)
                start_x = absolute.x
                start_y = absolute.y
                relative = position
                menu.translation.x = absolute.x - menu.radius
                menu.translation.y = absolute.y - menu.radius

                menu.setPointer(0f, 0f)
            }

            override fun onMouseDrag(position: Point, offset: Point) {
                val absolute = trigger.getAbsolutePosition(position.x, position.y)
                menu.setPointer(absolute.x - start_x, absolute.y - start_y)
            }

            override fun onGlobalMouseUp(position: Point) {
                if (menu.parent != null) {
                    UINodeUtil.getWindow(trigger).overlay.remove(menu)
                    val selected = menu.getSelected()
                    val rel = relative
                    if (selected != null && rel != null)
                        handler.onSelect(selected, rel)
                }
            }
        })
    }

    fun handle(event: UIEvent) {
        mouse.handle(event)
    }

    fun addMenuItem(item: ContextMenuItem) {
        menu.addMenuItem(item)
    }
}
