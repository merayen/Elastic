package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

internal class TrackList : AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox()) {
    var layoutWidth = 0f

    override fun onUpdate() {
        super.onUpdate()
        for (obj in search.children)
            (obj as Track).layoutWidth = layoutWidth - 10

        placement.maxWidth = layoutWidth
    }
}
