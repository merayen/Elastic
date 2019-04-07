package net.merayen.elastic.backend.logicnodes.list.compressor_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode

class LogicNode : BaseLogicNode() {
	override fun onInit() {}
	override fun onData(data: MutableMap<String, Any>?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onPrepareFrame(data: MutableMap<String, Any>?) {}

	override fun onFinishFrame(data: MutableMap<String, Any>?) {
		sendDataToUI(data)
	}

	override fun onParameterChange(key: String?, value: Any?) = set(key, value)

	override fun onCreate() {
		createPort(PortDefinition("input"))
		createPort(PortDefinition("sidechain"))
		createPort(PortDefinition("output", Format.AUDIO))
		createPort(PortDefinition("attenuation", Format.SIGNAL))

		set("inputAmplitude", 1f);
		set("inputSidechainAmplitude", 1f);
		set("outputAmplitude", 1f);
		set("attack", 0.1f)
		set("release", 0.1f)
		set("threshold", 1f)
	}
}