package net.merayen.elastic.uinodes.list.midi_1

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.uinodes.list.midi_1.editor.Editor

class UI : UINode(), INodeEditable {
	init {
		layoutWidth = 80f
		layoutHeight = 40f
	}

	override fun onInit() {
		super.onInit()
		titlebar.title = "MIDI"
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "in") {
			port.translation.y = 20f
		} else if (port.name == "out") {
			port.translation.x = layoutWidth
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: BaseNodeData) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(instance: BaseNodeData) {}

	override fun getNodeEditor() = Editor(nodeId)
}
