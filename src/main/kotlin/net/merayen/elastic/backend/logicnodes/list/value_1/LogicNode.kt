package net.merayen.elastic.backend.logicnodes.list.value_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createOutputPort("elapsed", Format.SIGNAL)
		createOutputPort("beat", Format.SIGNAL)
	}

	override fun onRemove() { }
	override fun onConnect(port: String?) { }
	override fun onDisconnect(port: String?) { }
	override fun onData(data: NodeDataMessage?) { }

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}

	override fun onFinishFrame(data: OutputFrameData?) { }
}