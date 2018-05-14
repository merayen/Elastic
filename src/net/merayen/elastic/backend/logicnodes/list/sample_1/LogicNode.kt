package net.merayen.elastic.backend.logicnodes.list.sample_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode

class LogicNode : BaseLogicNode() {
	override fun onCreate() {
		createPort(object: PortDefinition() {
			init {
				name = "control"
			}
		})

		createPort(object : PortDefinition() {
			init {
				name = "out"
				format = Format.AUDIO
				output = true
			}
		})
	}

	override fun onInit() {}
	override fun onParameterChange(key: String?, value: Any?) = set(key, value)
	override fun onData(data: MutableMap<String, Any>?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onPrepareFrame(data: MutableMap<String, Any>?) {}
	override fun onFinishFrame(data: MutableMap<String, Any>?) {}
}