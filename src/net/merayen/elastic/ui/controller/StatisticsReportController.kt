package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.StatisticsReportMessage
import net.merayen.elastic.ui.objects.top.views.statisticsview.StatisticsView
import net.merayen.elastic.util.Postmaster

class StatisticsReportController(gate: Gate) : Controller(gate) {
	class Hello : Postmaster.Message()

	private val statisticsViews: List<StatisticsView>
		get() {
			val result = ArrayList<StatisticsView>()

			for (view in getViews(StatisticsView::class.java)) {
				result.add(view)
			}

			return result
		}

	override fun onInit() {}

	override fun onMessageFromBackend(message: Postmaster.Message) {
		if (message is StatisticsReportMessage) {
			for (x in statisticsViews)
				x.handleStatisticsReportMessage(message)
		}
	}

	override fun onMessageFromUI(message: Postmaster.Message) {}
}