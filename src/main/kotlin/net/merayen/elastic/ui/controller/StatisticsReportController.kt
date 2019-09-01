package net.merayen.elastic.ui.controller

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.logicnodes.list.output_1.OutputNodeStatisticsData
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.StatisticsReportMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.statisticsview.StatisticsView

class StatisticsReportController(top: Top) : Controller(top) {
	class Hello

	private val statisticsViews: List<StatisticsView>
		get() {
			val result = ArrayList<StatisticsView>()

			for (view in getViews(StatisticsView::class.java))
				result.add(view)

			return result
		}

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		if (message is StatisticsReportMessage) {
			for (x in statisticsViews)
				x.handleStatisticsReportMessage(message)
		} else if (message is NodeDataMessage) {
			val nodeProperties = NodeProperties(top.netlist)

			val nodeId = message.nodeId
			val node = top.netlist.getNode(nodeId)
			if (node != null && nodeProperties.getName(node) == "output") {
				if (message is OutputNodeStatisticsData) {
					// Send it to all the Statistics views
					for(statisticsView in getViews(StatisticsView::class.java))
						statisticsView.handleOutputNodeStatistics(message)
				}
			}
		}
	}

	override fun onMessageFromUI(message: ElasticMessage) {}
}