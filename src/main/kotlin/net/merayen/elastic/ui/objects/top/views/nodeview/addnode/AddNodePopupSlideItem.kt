package net.merayen.elastic.ui.objects.top.views.nodeview.addnode

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.popupslide.PopupSlideItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.uinodes.UINodeInformation
import net.merayen.elastic.util.MutablePoint

@Deprecated("Should be replaced by AddNodeWindow")
internal class AddNodePopupSlideItem(handler: Handler) : PopupSlideItem(Content()) {
    internal interface Handler {
        fun onSelectCategory(category: String)
    }

    private class Content : PopupSlideItem.Content() {

        private val list = AutoLayout(LayoutMethods.HorizontalBox(10f, 390f))
        internal var handler: Handler? = null

        internal interface Handler {
            fun onSelect(text: String)
        }

        override fun onInit() {
            addCategory("Import")

            for (category in UINodeInformation.getCategories())
                addCategory(category)

            add(list)
        }

        private fun addCategory(text: String) {
            list.add(object : UIObject() {
                var mouse = MouseHandler(this)
                var over: Boolean = false

                override fun onInit() {
                    mouse.setHandler(object : MouseHandler.Handler() {
                        override fun onMouseOver() {
                            over = true
                        }

                        override fun onMouseOut() {
                            over = false
                        }

                        override fun onMouseClick(position: MutablePoint) {
                            handler?.onSelect(text)
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
                    draw.text(text, width / 2 - draw.getTextWidth(text) / 2, 20f)
                }

                override fun onEvent(event: UIEvent) {
                    mouse.handle(event)
                }
            })
        }

        override fun onDraw(draw: Draw) {
            super.onDraw(draw)

            draw.setColor(200, 200, 200)
            draw.setFont("", 12f)
            draw.text("Will show search and categories", 20f, 20f)
        }
    }

    init {

        (content as Content).handler = object : Content.Handler {
            override fun onSelect(text: String) {
                title.text = "Category: $text"
                handler.onSelectCategory(text)
            }
        }
    }

    override fun onInit() {
        super.onInit()

        layoutWidth = 408f
        layoutHeight = 500f

        title.text = "Choose node category"
    }
}
