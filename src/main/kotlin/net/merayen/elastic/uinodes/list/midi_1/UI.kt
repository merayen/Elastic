package net.merayen.elastic.uinodes.list.midi_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.uinodes.list.midi_1.editor.Editor

class UI : UINode(), INodeEditable, EasyMotionBranch {
	init {
		layoutWidth = 80f
		layoutHeight = 40f
	}

	override fun onInit() {
		super.onInit()
		titlebar.title = "Notes"

		easyMotionBranch.controls[setOf(KeyboardEvent.Keys.Q)] = Branch.Control {
			Branch.Control.STEP_BACK
		}
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

	override fun onProperties(message: BaseNodeProperties) {}

	override fun onData(message: NodeDataMessage) {}

	override fun getNodeEditor() = Editor(nodeId)
}
