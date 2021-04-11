package net.merayen.elastic.backend.logicnodes.list.output_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() { // TODO delete
	override fun onInit() {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onParameterChange(instance: BaseNodeProperties) {
		updateProperties(instance)
	}
	override fun onRemove() {}
	override fun onData(data: NodeDataMessage) {
	}
}
