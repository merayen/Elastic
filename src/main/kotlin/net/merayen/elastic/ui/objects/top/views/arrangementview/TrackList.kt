package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

internal class TrackList : AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox()), FlexibleDimension {
    override var layoutWidth = 100f
    override var layoutHeight = 50f

    override fun onUpdate() {
        super.onUpdate()
        for (obj in search.children)
            (obj as TrackPane).layoutWidth = layoutWidth

        placement.maxWidth = layoutWidth
    }
}
