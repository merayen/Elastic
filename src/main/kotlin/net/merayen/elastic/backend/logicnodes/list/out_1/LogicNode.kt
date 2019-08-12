package net.merayen.elastic.backend.logicnodes.list.out_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onCreate() {
		createInputPort("in")
	}

	override fun onInit() {}
	override fun onParameterChange(instance: BaseNodeData) = updateProperties(instance)
	override fun onData(data: NodeDataMessage) {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData?) {}
}
