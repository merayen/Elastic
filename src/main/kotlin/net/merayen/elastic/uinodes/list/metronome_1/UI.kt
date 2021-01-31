package net.merayen.elastic.uinodes.list.metronome_1

import net.merayen.elastic.backend.logicnodes.list.metronome_1.MetronomeBeatMessage
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override var layoutWidth = 100f
	override var layoutHeight = 60f

	private val beatIndicator = BeatIndicator()

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"audio" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
			"midi" -> {
				port.translation.x = layoutWidth
				port.translation.y = 40f
			}
		}
	}

	override fun onInit() {
		super.onInit()
		titlebar.title = "Metronome"

		beatIndicator.translation.x = 20f
		beatIndicator.translation.y = 30f
		add(beatIndicator)
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onProperties(message: BaseNodeProperties) {}

	override fun onData(message: NodeDataMessage) {
		if (message is MetronomeBeatMessage)
			beatIndicator.handleMessage(message)
	}
}