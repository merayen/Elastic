package net.merayen.elastic.backend.logicnodes.list.signalgenerator_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	override fun onCreate() {
		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "frequency"
			}
		})

		createPort(object : BaseLogicNode.PortDefinition() {
			init {
				name = "output"
				format = Format.AUDIO
				output = true
			}
		})

		(properties as Data).frequency = 440f
	}

	override fun onInit() {}

	override fun onParameterChange(instance: BaseNodeData) { // Parameter change from UI
		this.updateProperties(instance) // Accept anyway
	}

	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onFinishFrame(data: OutputFrameData) {}
	override fun onRemove() {}
	override fun onData(data: NodeDataMessage) {}
}
