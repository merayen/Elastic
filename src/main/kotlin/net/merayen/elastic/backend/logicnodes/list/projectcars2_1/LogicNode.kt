package net.merayen.elastic.backend.logicnodes.list.projectcars2_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createOutputPort("rpm", Format.SIGNAL)
		createOutputPort("nm", Format.SIGNAL)
		createOutputPort("hp", Format.SIGNAL)
		createOutputPort("running", Format.SIGNAL) // 1.0f if game is running, no pause screen
		createOutputPort("engine_on", Format.SIGNAL) // 1.0f if engine is on
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
}