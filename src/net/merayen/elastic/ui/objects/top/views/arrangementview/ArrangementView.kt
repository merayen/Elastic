package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.objects.top.views.View

class ArrangementView : View() {
    private val bar = ArrangementViewBar()
    private val arrangement = Arrangement()

    override fun onInit() {
        super.onInit()

        add(bar)
        add(arrangement)
        arrangement.translation.y = 20f
    }

    override fun onUpdate() {
        super.onUpdate()
        arrangement.layoutWidth = layoutWidth
        arrangement.layoutHeight = layoutHeight - 20
    }

    override fun cloneView(): View {
        return ArrangementView()
    }
}
