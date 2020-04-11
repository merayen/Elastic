package net.merayen.elastic.backend.logicnodes.list.oscilloscope_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createInputPort("in")
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onDisconnect(port: String?) {}
	override fun onConnect(port: String?) {}
	override fun onRemove() {}
	override fun onParameterChange(instance: BaseNodeProperties?) {
		instance as Properties
		updateProperties(instance)
	}

	override fun onFinishFrame(data: OutputFrameData?) {
		if (data is OscilloscopeSignalDataMessage)
			sendDataToUI(data)
	}
}