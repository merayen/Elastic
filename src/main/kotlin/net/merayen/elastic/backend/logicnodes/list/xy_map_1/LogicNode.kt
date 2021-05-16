package net.merayen.elastic.backend.logicnodes.list.xy_map_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage

class LogicNode : BaseLogicNode() {
	override fun onInit() {
		createInputPort("in")
		createInputPort("fac")
		createOutputPort("out", Format.SIGNAL)
	}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		instance as Properties

		instance.curve?.let { sendToDSP(CurveData(id, floatArrayOf(1f, 2f, 3f))) } // TODO implement

		updateProperties(instance)
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
}