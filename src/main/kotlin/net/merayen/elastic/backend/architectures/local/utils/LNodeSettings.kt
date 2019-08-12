package net.merayen.elastic.backend.architectures.local.utils

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.util.treesettings.InheritanceNode

class LNodeSettings {
	private val cache = HashMap<LocalNode, InheritanceNode>()

	fun get(lnode: LocalNode) = cache[lnode] ?: loadInheritanceNode(lnode)

	private fun loadInheritanceNode(lnode: LocalNode): InheritanceNode {
		val inheritanceNode = InheritanceNode()

		val parent = lnode.parent
		if (parent != null)
			inheritanceNode.parent = loadInheritanceNode(parent)

		cache[lnode] = inheritanceNode

		lnode.getParameter("settings")

		return inheritanceNode
	}
}