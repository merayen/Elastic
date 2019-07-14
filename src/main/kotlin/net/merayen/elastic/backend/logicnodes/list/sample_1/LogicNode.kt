package net.merayen.elastic.backend.logicnodes.list.sample_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onCreate() {
		createPort(PortDefinition("control"))
		createPort(PortDefinition("out", Format.AUDIO))
	}

	override fun onInit() {}
	override fun onParameterChange(key: String?, value: Any?) = set(key, value)
	override fun onData(data: NodeDataMessage) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData) {}
}