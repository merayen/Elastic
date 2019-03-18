package net.merayen.elastic.ui.objects.top.views.statisticsview

import net.merayen.elastic.system.intercom.StatisticsReportMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.views.View

class StatisticsView : View() {
	private val bar = StatisticsViewBar()

	private var avgTime = 0.0
	private var maxTime = 0.0

	override fun cloneView() = StatisticsView()

	override fun onInit() {
		super.onInit()

		add(bar)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(1f, 0f, 1f)
		draw.setFont("", 20f)
		draw.text("Avg: ${"%.3f".format(avgTime * 1000)}ms", 50f, 100f)
		draw.text("Max: ${avgTime * 1000}ms", 50f, 150f)
	}

	fun handleStatisticsReportMessage(message: StatisticsReportMessage) {
		avgTime = message.avgFrameTime
		maxTime = message.maxFrameTime
	}
}