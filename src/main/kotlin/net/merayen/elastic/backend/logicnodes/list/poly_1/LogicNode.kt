package net.merayen.elastic.backend.logicnodes.list.poly_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.backend.nodes.GroupLogicNode
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode(), GroupLogicNode {
	override fun onCreate() {
		createInputPort("input")
		createOutputPort("output", Format.AUDIO)
	}

	override fun onInit() {}
	override fun onParameterChange(instance: BaseNodeData) = updateProperties(instance) // Acknowledge anyway
	override fun onData(data: NodeDataMessage) {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData?) {}
}
