package net.merayen.elastic.ui.controller

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.StatisticsReportMessage
import net.merayen.elastic.ui.objects.top.views.statisticsview.StatisticsView
import net.merayen.elastic.util.NodeUtil
import net.merayen.elastic.util.Postmaster

class StatisticsReportController(gate: Gate) : Controller(gate) {
	class Hello : Postmaster.Message()

	private val statisticsViews: List<StatisticsView>
		get() {
			val result = ArrayList<StatisticsView>()

			for (view in getViews(StatisticsView::class.java))
				result.add(view)

			return result
		}

	override fun onInit() {}

	override fun onMessageFromBackend(message: Postmaster.Message) {
		if (message is StatisticsReportMessage) {
			for (x in statisticsViews)
				x.handleStatisticsReportMessage(message)
		} else if (message is NodeDataMessage) {
			val nodeProperties = NodeProperties(gate.netlist)

			val nodeId = message.nodeId
			val node = gate.netlist.getNode(nodeId)
			if (node != null && nodeProperties.getName(node) == "output") {
				val statistics = message.value["statistics"] as? Map<String, Any>

				if (statistics != null)
					;//println(statistics["available_before_min"])
			}
		}
	}

	override fun onMessageFromUI(message: Postmaster.Message) {}
}