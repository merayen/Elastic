package net.merayen.elastic.ui.objects.top.marks

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.logicnodes.list.group_1.Properties
import net.merayen.elastic.backend.nodes.mapToLogicNodeProperties
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.objects.top.views.View

/**
 * Handles marks.
 * Uses the top-most node, a group-node's properties to store the marks.
 * Hence, the marks are project-global.
 *
 * E.g marks can be set on a node, or even on a piano note inside a node. Should also be able to mark knobs.
 *
 * Reads data directly from top-most node, but sends a group_1.Properties in a NodePropertyMessage to update the node.
 */
class MarksManager(private val view: View) {
	private val top by lazy {
		view.search.top!!
	}

	private val topNode: Node
		get() = NetListUtil(top.netlist).topGroupNode

	/**
	 * Retrieve the marks stored on the top-most group-node.
	 */
	private val marks: MutableList<Properties.Mark>
		get() {
			// Retrieve group node's data via its Properties-class instead of direct accessing its properties-map
			val np = NodeProperties(top.netlist)
			val properties = mapToLogicNodeProperties(np.getName(topNode), np.getVersion(topNode), topNode.properties) as Properties

			return properties.marks ?: ArrayList()
		}

	/**
	 * Set a mark. This will send a BaseNodeProperties to the topmost group_1 node.
	 * Send null in what to delete.
	 *
	 * @param mark The name of the mark
	 * @param identifier The identifier of the mark. Send null to delete mark
	 */
	fun mark(mark: Char, nodeId: String, identifier: String?) {
		val marks = marks

		marks.removeIf { it.mark == mark }

		if (identifier != null)
			marks.add(Properties.Mark(mark = mark, nodeId = nodeId, identifier = identifier))

		val message = NodePropertyMessage(
			topNode.id,
			Properties(marks = marks)
		)

		view.sendMessage(message)
	}

	fun get(mark: Char) = marks.firstOrNull { it.mark == mark }
}