package net.merayen.elastic.backend.logicnodes.list.frequency_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	private var requestSpectrumData = false

	override fun onInit() {
		createInputPort("in")
	}

	override fun onParameterChange(instance: BaseNodeProperties?) { }

	override fun onData(data: NodeDataMessage?) {
		if (data is FrequencyRequestMessage)
			requestSpectrumData = true
	}

	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}

	override fun onPrepareFrame(): InputFrameData {
		if (requestSpectrumData) {
			requestSpectrumData = false
			return FrequencyInputFrameData(id)
		}
		return super.onPrepareFrame()
	}

	override fun onFinishFrame(data: OutputFrameData?) {
		if (data is FrequencyOutputFrameData) {
			sendDataToUI(FrequencyUpdateMessage(id, data.spectrum))
		}
	}
}