package net.merayen.elastic.backend.architectures.local.nodes.eq_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor

class LNode : LocalNode(LProcessor::class.java) {
	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onProcess(data: MutableMap<String, Any>?) {}
	override fun onParameter(key: String?, value: Any?) {}
	override fun onFinishFrame() {}
	override fun onDestroy() {}
}