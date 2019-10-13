package net.merayen.elastic.backend.architectures.local.nodes.cutoff_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.cutoff_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	var frequency = 1f
		private set

	var damping = 2f
		private set

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}

	override fun onProcess(data: InputFrameData?) {}

	override fun onParameter(instance: BaseNodeProperties?) {
		instance as Properties

		val frequency = instance.frequency
		val damping = instance.damping

		if (frequency != null)
			this.frequency = frequency

		if (damping != null)
			this.damping = damping
	}

	override fun onFinishFrame() {}
	override fun onDestroy() {}
}