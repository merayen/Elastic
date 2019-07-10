package net.merayen.elastic.backend.logicnodes.list.poly_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {

	override fun onCreate() {
		createPort(PortDefinition("input"))
		createPort(PortDefinition("output", Format.AUDIO))
	}

	override fun onInit() {}
	override fun onParameterChange(key: String, value: Any) = set(key, value) // Acknowledge anyway
	override fun onData(data: Any) {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}
	override fun onPrepareFrame(data: Map<String, Any>) {}
	override fun onFinishFrame(data: OutputFrameData) {}
}
