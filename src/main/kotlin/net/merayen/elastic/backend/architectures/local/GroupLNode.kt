package net.merayen.elastic.backend.architectures.local

import net.merayen.elastic.util.treesettings.InheritanceNode

/**
 * LNodes implementing GroupLNode are capable to hold other children nodes.
 * Those children nodes can get data from LNodes implementing GroupLNode, by using getParent()
 */
interface GroupLNode {
	fun getSettings(): InheritanceNode
}