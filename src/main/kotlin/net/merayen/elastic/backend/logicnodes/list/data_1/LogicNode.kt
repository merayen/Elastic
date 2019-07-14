package net.merayen.elastic.backend.logicnodes.list.data_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onCreate() {}
	override fun onInit() {}
	override fun onData(data: NodeDataMessage) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData) {}

	override fun onParameterChange(key: String?, value: Any?) {
		set(key, value)
	}
}