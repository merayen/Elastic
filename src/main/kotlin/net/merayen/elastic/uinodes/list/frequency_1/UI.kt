package net.merayen.elastic.uinodes.list.frequency_1

import net.merayen.elastic.backend.logicnodes.list.frequency_1.FrequencyRequestMessage
import net.merayen.elastic.backend.logicnodes.list.frequency_1.FrequencyUpdateMessage
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val frequencySpectrum = FrequencySpectrum()
	private var nextSpectrumRequest = 0L

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> {
				port.translation.y = 20f
			}
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onProperties(properties: BaseNodeProperties) {}

	override fun onInit() {
		super.onInit()

		layoutWidth = 200f
		layoutHeight = 130f

		frequencySpectrum.translation.x = 10f
		frequencySpectrum.translation.y = 20f
		frequencySpectrum.layoutWidth = 180f
		frequencySpectrum.layoutHeight = 100f
		add(frequencySpectrum)
	}

	override fun onUpdate() {
		super.onUpdate()

		// If this method is run, it means we are shown to the user. We request data from the backend every second
		if (nextSpectrumRequest < System.currentTimeMillis()) {
			sendMessage(FrequencyRequestMessage(nodeId))
			nextSpectrumRequest = System.currentTimeMillis() + 1000
		}
	}

	override fun onData(message: NodeDataMessage) {
		if (message is FrequencyUpdateMessage) {
			val poles = message.spectrum
			if (poles != null)
				frequencySpectrum.applyPoles(poles)
		}
	}
}