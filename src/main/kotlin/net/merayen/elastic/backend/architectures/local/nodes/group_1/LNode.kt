package net.merayen.elastic.backend.architectures.local.nodes.group_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.util.treesettings.InheritanceNode

class LNode : LocalNode(LProcessor::class.java), GroupLNode {
	override fun getSettings(): InheritanceNode {
		val parent = parent
		val node: InheritanceNode
		if (parent != null) {
			if (parent !is GroupLNode)
				throw RuntimeException("Parent LNode ${javaClass.name} does not implement GroupLNode")

			node = parent.getSettings()
		} else {
			node = InheritanceNode()
		}

		// Insert our settings
		TODO("Implement")
		//node.put()
	}

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor) {}
	override fun onProcess(data: InputFrameData) {}
	override fun onParameter(instance: BaseNodeData) {}
	override fun onFinishFrame() {}
	override fun onDestroy() {}
}
