package net.merayen.elastic.backend.architectures.local.nodes.math_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.math_1.Mode
import net.merayen.elastic.backend.logicnodes.list.math_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	var mode = Mode.ADD
		private set

	var aValue = 0f
	var bValue = 0f

	override fun onProcess(data: InputFrameData?) {}

	override fun onParameter(instance: BaseNodeProperties?) {
		instance as Properties

		aValue = instance.aValue ?: aValue
		bValue = instance.bValue ?: bValue

		val mode = instance.mode
		if (mode != null)
			this.mode = Mode.valueOf(mode)
	}

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onFinishFrame() {}
	override fun onDestroy() {}
}