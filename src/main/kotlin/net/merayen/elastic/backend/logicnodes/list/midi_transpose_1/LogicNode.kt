package net.merayen.elastic.backend.logicnodes.list.midi_transpose_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onCreate() {
		createPort(PortDefinition("in"))
		createPort(PortDefinition("out", Format.MIDI))
	}

	override fun onInit() {}

	override fun onParameterChange(key: String?, value: Any?) {
		set(key, value)
	}

	override fun onData(data: Any) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onPrepareFrame(data: MutableMap<String, Any>?) {}
	override fun onFinishFrame(data: OutputFrameData) {}
}