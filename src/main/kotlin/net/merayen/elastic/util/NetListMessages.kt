package net.merayen.elastic.util

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.nodes.logicNodePropertiesToMap
import net.merayen.elastic.backend.nodes.mapToLogicNodeProperties
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.*
import java.util.*

/**
 * Builds/changes a NetList by messaging put into it.
 * Can also generate messages of the current NetList
 * Helper class to restore NetList into either UI or backend.
 * Iterates through the NetList and generates messages that can be consumed by the UI or backend so they can generate their own NetList.
 *
 * TODO support restoring from just one certain owner, so that we don't generate messages for all groups etc too, if not really wanted. Hmm...
 */
object NetListMessages { // TODO silly name, fix
	/**
	 * Creates messages for the whole NetList, including groups, subgroups etc.
	 * Returned messages will need to be executed in the order returned.
	 */
	fun disassemble(netlist: NetList): List<ElasticMessage> {
		val result = ArrayList<ElasticMessage>()
		val np = NodeProperties(netlist)

		// Restore the nodes
		for (node in netlist.nodes) {

			// Restore the Node() itself
			result.add(CreateNodeMessage(node.id, np.getName(node), np.getVersion(node), np.getParent(node)))

			// Restore the Node()'s ports
			for (port in netlist.getPorts(node)) {
				val p = netlist.getPort(node, port)
				result.add(CreateNodePortMessage(
					node.id,
					port,
					np.isOutput(p),
					np.getFormat(p)
				))
			}
		}

		// Restore all the connections between the Node()s
		for (line in netlist.lines)
			result.add(NodeConnectMessage(line.node_a.id, line.port_a, line.node_b.id, line.port_b))

		// Restore the Node()'s properties
		for (node in netlist.nodes) {
			val data = mapToLogicNodeProperties(np.getName(node), np.getVersion(node), node.properties)
			result.add(NodePropertyMessage(node.id, data))
		}

		return result
	}

	/**
	 * Restore from NetList, but filter by a certain group.
	 * Delete?
	 */
	fun disassemble(netlist: NetList, parent_node_id: String): List<ElasticMessage> {
		val filtered = netlist.copy()
		val np = NodeProperties(filtered)

		for (node in netlist.nodes) // Remove nodes that are not in the group group_id
			if (np.getParent(node) !== parent_node_id)
				filtered.remove(node)

		return disassemble(filtered)
	}

	/**
	 * Change a NetList by a message.
	 * All messages should be sent in correct order, with no loss, or we may have synchronization issues.
	 */
	fun apply(netlist: NetList, message: ElasticMessage) {
		val np = NodeProperties(netlist)

		if (message is CreateNodeMessage) {
			val node = netlist.createNode(message.node_id)
			np.setName(node, message.name)
			np.setVersion(node, message.version)
			np.setParent(node, message.parent)

		} else if (message is CreateNodePortMessage) {
			val port = netlist.createPort(message.node_id, message.port)

			if (message.output) {
				np.setOutput(port)
				np.setFormat(port, message.format)
			}

		} else if (message is RemoveNodeMessage) {
			netlist.remove(message.node_id)

		} else if (message is RemoveNodePortMessage) {
			netlist.removePort(message.node_id, message.port)

		} else if (message is NodeConnectMessage) {
			netlist.connect(message.node_a, message.port_a, message.node_b, message.port_b)

		} else if (message is NodeDisconnectMessage) {
			netlist.disconnect(message.node_a, message.port_a, message.node_b, message.port_b)

		} else if (message is NodePropertyMessage) {
			// Merges in the non-null properties from NodeParameterMessage
			val node = netlist.getNode(message.node_id)
			val name = np.getName(node)
			val version = np.getVersion(node)

			val data = mapToLogicNodeProperties(name, version, node.properties)
			ClassInstanceMerger.merge(message.instance, data)

			val updatedProperties = logicNodePropertiesToMap(name, version, data)

			node.properties.clear()
			node.properties.putAll(updatedProperties)
		}
	}
}
