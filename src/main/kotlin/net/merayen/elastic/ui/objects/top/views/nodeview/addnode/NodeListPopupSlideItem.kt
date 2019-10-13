package net.merayen.elastic.ui.objects.top.views.nodeview.addnode

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.popupslide.PopupSlideItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.uinodes.BaseInfo
import net.merayen.elastic.uinodes.UINodeInformation
import net.merayen.elastic.util.MutablePoint

internal class NodeListPopupSlideItem(category: String, handler: Handler) : PopupSlideItem(Content(category)) {
    internal interface Handler {
        fun onSelect(info: BaseInfo)
    }

    private class Content internal constructor(internal val category: String) : PopupSlideItem.Content() {

        internal var handler: Handler? = null

        private val list = AutoLayout(LayoutMethods.HorizontalBox(10f, 400f))

        internal interface Handler {
            fun onSelect(info: BaseInfo)
        }

        private fun setHandler(handler: Handler) {
            this.handler = handler
        }

        override fun onInit() {
            add(list)

            for (info in UINodeInformation.getNodeInfos())
                for (node_category in info.categories)
                    if (node_category == category)
                        addNode(info)
        }

        private fun addNode(info: BaseInfo) {
            list.add(object : UIObject() {
                internal var mouse = MouseHandler(this)
                internal var over: Boolean = false

                override fun onInit() {
                    mouse.setHandler(object : MouseHandler.Handler() {
                        override fun onMouseOver() {
                            over = true
                        }

                        override fun onMouseOut() {
                            over = false
                        }

                        override fun onMouseClick(position: MutablePoint) {
                            handler?.onSelect(info)
                        }
                    })
                }

                override fun onDraw(draw: Draw) {
                    val width = 100f
                    val height = 100f

                    if (over)
                        draw.setColor(150, 200, 150)
                    else
                        draw.setColor(150, 150, 150)
                    draw.fillRect(0f, 0f, width, height)

                    draw.setColor(50, 50, 50)
                    draw.setStroke(2f)
                    draw.rect(0f, 0f, width, height)

                    draw.setColor(0, 0, 0)
                    draw.setFont("", 16f)
                    draw.text(info.name, width / 2 - draw.getTextWidth(info.name) / 2, 20f)
                }

                override fun onEvent(event: UIEvent) {
                    mouse.handle(event)
                }
            })
        }
    }

    init {
        (content as Content).handler = object : Content.Handler {
            override fun onSelect(info: BaseInfo) {
                handler.onSelect(info)
            }
        }

        layoutWidth = 400f
        layoutHeight = 400f

        title.text = "Select node"
    }
}
