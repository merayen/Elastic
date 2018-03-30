package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Button
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

internal class Arrangement : UIObject() {
    var layoutWidth: Float = 0f
    var layoutHeight: Float = 0f
    private val trackList = TrackList()
    private val arrangementListScroll = Scroll(trackList)
    private val buttonBar = AutoLayout(LayoutMethods.HorizontalBox(5f, 100000f))

    override fun onInit() {
        add(arrangementListScroll)
        add(buttonBar)

        arrangementListScroll.translation.y = 20f

        buttonBar.add(object : Button() {
            init {
                label = "New track"
                setHandler({
                    val track = Track()
                    trackList.add(track)
                    track.handler = object : Track.Handler {
                        override fun onRemove() = trackList.remove(track)
                    }
                })
            }
        })
    }

    override fun onUpdate() {
        trackList.layoutWidth = layoutWidth
        arrangementListScroll.layoutWidth = layoutWidth
        arrangementListScroll.layoutHeight = layoutHeight - 20
    }
}
