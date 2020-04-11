package net.merayen.elastic.backend.logicnodes.list.math_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
	private var mode: Mode? = null

	override fun onInit() {
		createOutputPort("out", Format.SIGNAL)
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onDisconnect(port: String?) {}
	override fun onConnect(port: String?) {}
	override fun onRemove() {}

	override fun onParameterChange(instance: BaseNodeProperties?) {
		instance as Properties

		val mode = instance.mode

		if (mode != null && Mode.valueOf(mode) != this.mode) {
			this.mode = Mode.valueOf(mode)
			updatePorts()
		}
		updateProperties(instance)
	}

	private fun updatePorts() {
		val neededPorts = when (mode!!) {
			Mode.ADD, Mode.SUBTRACT, Mode.MULTIPLY, Mode.DIVIDE, Mode.LOG, Mode.MODULO, Mode.POWER -> listOf("a", "b")
			Mode.SIN, Mode.COS, Mode.TAN, Mode.ASIN, Mode.ACOS, Mode.ATAN -> listOf("a")
		}

		for (port in ports)
			if (port != "out" && port !in neededPorts)
				removePort(port)

		val ports = ports
		for (port in neededPorts)
			if (port !in ports)
				createInputPort(port)
	}

	override fun onFinishFrame(data: OutputFrameData?) {}
}