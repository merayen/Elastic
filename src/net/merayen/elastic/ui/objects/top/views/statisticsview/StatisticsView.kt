package net.merayen.elastic.ui.objects.top.views.statisticsview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.views.View

class StatisticsView : View() {
	private val bar = StatisticsViewBar()

	override fun cloneView() = StatisticsView()

	override fun onInit() {
		super.onInit()

		add(bar)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)


	}
}