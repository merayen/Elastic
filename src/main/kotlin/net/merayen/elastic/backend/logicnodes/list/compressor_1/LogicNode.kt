package net.merayen.elastic.backend.logicnodes.list.compressor_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onInit() {}
	override fun onData(data: NodeDataMessage) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onPrepareFrame(): InputFrameData = InputFrameData(id)

	override fun onFinishFrame(data: OutputFrameData) {
		sendDataToUI(data)
	}

	override fun onParameterChange(instance: BaseNodeData) = updateProperties(instance)

	override fun onCreate() {
		createInputPort("input")
		createInputPort("sidechain")
		createOutputPort("output", Format.AUDIO)
		createOutputPort("attenuation", Format.SIGNAL)
	}
}