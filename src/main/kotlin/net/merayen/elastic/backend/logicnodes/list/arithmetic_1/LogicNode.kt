package net.merayen.elastic.backend.logicnodes.list.arithmetic_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onData(data: NodeDataMessage?) {}
	override fun onInit() {}
	override fun onDisconnect(port: String?) {}
	override fun onConnect(port: String?) {}
	override fun onRemove() {}
	override fun onParameterChange(instance: BaseNodeProperties?) {}
	override fun onFinishFrame(data: OutputFrameData?) {}
}