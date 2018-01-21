package net.merayen.elastic.backend.logicnodes.list.poly_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode

class LogicNode : BaseLogicNode() {

	override fun onCreate() {
		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "input"
			}
		})

		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "output"
				format = Format.AUDIO
				output = true
			}
		})
	}

	override fun onInit() {}

	override fun onParameterChange(key: String, value: Any) = set(key, value) // Acknowledge anyway

	override fun onData(data: Map<String, Any>) {}

	override fun onConnect(port: String) {}

	override fun onDisconnect(port: String) {}

	override fun onRemove() {}

	override fun onPrepareFrame(data: Map<String, Any>) {}

	override fun onFinishFrame(data: Map<String, Any>) {}
}
