package net.merayen.elastic.ui.objects.popupslide

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

abstract class PopupSlideItem(val content: Content) : UIObject() {

    var layoutWidth = 100f
    var layoutHeight = 100f

    val title = Title()

    open class ContentBase : UIObject() {
        var layoutWidth: Float = 0.toFloat()
        var layoutHeight: Float = 0.toFloat() // Calculated
    }

    class Title : ContentBase() {
        var text = ""

        override fun onDraw(draw: Draw) {
            draw.setColor(250, 250, 250)
            draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

            draw.setColor(0, 0, 0)
            draw.setFont("", 12f)
            draw.text(text, 10f, 15f)
        }
    }

    open class Content : ContentBase() { // TODO remove() Content when not in foreground
        override fun onDraw(draw: Draw) {
            draw.setColor(100, 100, 100)
            draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
        }
    }

    override fun onInit() {
        add(title)
        add(content)

        title.translation.x = 4f
        title.translation.y = 4f
        content.translation.x = 4f
        content.translation.y = 28f
    }

    override fun onUpdate() {
        title.layoutWidth = layoutWidth - 8
        title.layoutHeight = 20f
        content.layoutWidth = layoutWidth - 8
        content.layoutHeight = layoutHeight - 20f - 12f
    }

    fun makeActive(yes: Boolean) {
        if (yes && content.parent == null)
            add(content)
        else if (!yes && content.parent != null)
            remove(content)
    }

    override fun getWidth() = layoutWidth
    override fun getHeight() = layoutHeight
}
