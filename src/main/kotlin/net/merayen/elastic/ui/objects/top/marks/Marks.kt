package net.merayen.elastic.ui.objects.top.marks

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.backend.logicnodes.list.group_1.Properties
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.util.logDebug
import net.merayen.elastic.util.logError

class Marks(private val view: View) {
	private val marks = ArrayList<Properties.Mark>()

	private val top by lazy {
		view.search.top!!
	}

	/**
	 * Set a mark. This will send a BaseNodeProperties to the topmost group_1 node.
	 * Send null in what to delete.
	 */
	fun mark(mark: Char, what: String?) {
		val markString = mark.toString()

		marks.removeIf { it.mark == markString }

		if (what != null)
			marks.add(Properties.Mark(what))

		// Update the top-most node
		val message = NodePropertyMessage(
			getTopNode()?.id ?: return,
			Properties(marks = ArrayList(marks))
		)

		view.sendMessage(message)
	}

	/**
	 * Send all NodePropertyMessages to us.
	 */
	fun handleMessage(message: NodePropertyMessage) {
		val instance = message.instance
		val topNode = getTopNode() ?: return

		if (instance is Properties) {
			if (message.node_id == topNode.id) {
				val sourceMarks = instance.marks
				if (sourceMarks != null) {
					marks.clear()
					for (mark in marks) {
						logDebug(this, "Adding mark $mark")
						marks.add(mark)
					}
				}
			}
		}
	}

	private fun getTopNode(): Node? {
		val topNodes = NetListUtil(top.netlist).topNodes
		if (topNodes.size != 1) {
			logError(this, "Expected only 1 top node, got: ${topNodes.size} top nodes")
			return null
		}

		return topNodes.first()
	}
}