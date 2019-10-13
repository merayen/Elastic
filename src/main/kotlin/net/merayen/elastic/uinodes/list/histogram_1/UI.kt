package net.merayen.elastic.uinodes.list.histogram_1

import net.merayen.elastic.backend.logicnodes.list.histogram_1.HistogramUpdateMessage
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val histogram = Histogram()

	override fun onInit() {
		super.onInit()

		titlebar.title = "Histogram"

		layoutWidth = 100f
		layoutHeight = 150f

		histogram.translation.x = 10f
		histogram.translation.y = 20f
		histogram.layoutWidth = layoutWidth - 20f
		histogram.layoutHeight = layoutHeight - 40
		add(histogram)
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "data")
			port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeProperties) {}

	override fun onData(message: NodeDataMessage) {
		message as HistogramUpdateMessage
		val bucketsData = message.buckets
		if (bucketsData != null)
			histogram.buckets = bucketsData.clone()
	}
}