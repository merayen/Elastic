package net.merayen.elastic.backend.architectures.local.nodes.wave_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	private var type: Properties.Type? = null

	override fun onInit() {}

	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onProcess(data: InputFrameData?) {
		for (processor in processors) {
			processor as LProcessor
			processor.type = type
		}
	}

	override fun onParameter(instance: BaseNodeProperties?) {
		instance as Properties
		val type = instance.type

		if (type != null)
			this.type = Properties.Type.valueOf(type)
	}

	override fun onFinishFrame() {}
	override fun onDestroy() {}
}