package net.merayen.elastic.backend.logicnodes.list.meter_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createInputPort("in")
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onData(data: NodeDataMessage?) {
		if (data is MeterSignalData)
			sendDataToUI(data)
	}

	override fun onConnect(port: String?) {}

	override fun onDisconnect(port: String?) {}

	override fun onRemove() {}
}