package net.merayen.elastic.uinodes.list.from_midi_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch

class UI : UINode(), EasyMotionBranch {
	init {
		layoutWidth = 80f
		layoutHeight = 80f
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> port.translation.y = 20f
			"frequency" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
			"amplitude" -> {
				port.translation.x = layoutWidth
				port.translation.y = 40f
			}
			"sustain" -> {
				port.translation.x = layoutWidth
				port.translation.y = 60f
			}
		}
	}

	override fun onRemovePort(port: UIPort) { }
	override fun onProperties(properties: BaseNodeProperties) { }
	override fun onData(message: NodeDataMessage) { }
}