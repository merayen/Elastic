package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.backend.nodes.GroupLogicNode
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

/**
 * Doesn't do anything, other than having children.
 */
class LogicNode : BaseLogicNode(), GroupLogicNode {
	override fun onCreate() {}
	override fun onInit() {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData) {}
	override fun onData(data: NodeDataMessage) {
		if (data is SetBPMMessage) {
			(properties as Data).bpm = data.bpm
			acceptProperties()
		}
	}
	override fun onParameterChange(instance: BaseNodeData) = acceptProperties(instance)
}
