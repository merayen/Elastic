package net.merayen.elastic.backend.logicnodes.list.wave_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createInputPort("frequency")
		createOutputPort("out", Format.SIGNAL)

		(properties as Properties).frequency = 440f
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance) // Accept everything anyways
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
}