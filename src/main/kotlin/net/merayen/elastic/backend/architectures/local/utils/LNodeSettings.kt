package net.merayen.elastic.backend.architectures.local.utils

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.util.treesettings.InheritanceNode

class LNodeSettings {
	private val cache = HashMap<LocalNode, InheritanceNode>()

	fun get(lnode: LocalNode): InheritanceNode {
		TODO()
		if (lnode !in cache) {
			cache[lnode] = InheritanceNode()

		}
	}

	private fun getTree() {

	}
}