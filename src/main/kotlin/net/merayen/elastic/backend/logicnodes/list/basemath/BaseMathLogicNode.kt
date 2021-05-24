package net.merayen.elastic.backend.logicnodes.list.basemath

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * Base class for all math nodes.
 */
abstract class BaseMathLogicNode : BaseLogicNode() {
	abstract val minimumInputCount: Int
	abstract val maximumInputCount: Int

	final override fun onInit() {
		if (minimumInputCount > maximumInputCount || maximumInputCount < 1 || minimumInputCount < 1)
			error("Wrong port count")

		createOutputPort("out", Format.SIGNAL) // This port is always present on all math nodes

		for (i in 0 until minimumInputCount)
			createInputPort("in$i")
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onDisconnect(port: String?) {}
	override fun onConnect(port: String?) {
		// TODO See if we should create one more connectable port
	}
	override fun onRemove() {}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		updateProperties(instance)
	}
}