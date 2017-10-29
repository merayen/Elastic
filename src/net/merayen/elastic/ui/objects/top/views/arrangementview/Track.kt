package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.UIObject

internal class Track : UIObject() {

    var layoutWidth = 0f
    var layoutHeight = 100f
    var handler: Handler? = null

    private val header = TrackHeader()
    private val body = TrackBody()

    interface Handler {
        fun onRemove()
    }

    override fun onInit() {
        add(header)
        add(body)
        body.translation.x = 100f

        header.handler = object : TrackHeader.Handler {
            override fun onRemove() { handler?.onRemove() }
        }
    }

    override fun onDraw() {
        draw.setColor(0, 255, 0)
        draw.setStroke(1f)
        draw.rect(0f, 0f, layoutWidth, layoutHeight)
    }

    override fun onUpdate() {
        header.layoutWidth = layoutWidth
        header.layoutHeight = layoutHeight
        body.layoutWidth = layoutWidth - 100
        body.layoutHeight = layoutHeight
    }

    override fun getWidth() = layoutWidth

    override fun getHeight() = layoutHeight
}
