package net.merayen.elastic.backend.analyzer.node_dependency

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.netlist.NetList

/**
 * Creates a DependencyList from a NetList.
 *
 * Does not care about children nodes inside nodes. Use flattenDependencyList() to make group nodes depend on their
 * children.
 */
fun toDependencyList(netlist: NetList): DependencyList<String> {
	val result = DependencyList<String>()
	val np = NodeProperties(netlist)

	for (node in netlist.nodes) {
		val dependencies = HashSet<String>()

		for (port in np.getInputPorts(node)) {
			for (line in netlist.getConnections(node, port)) {
				if (line.node_a === node)
					dependencies.add(line.node_b.id)
				else if (line.node_b === node)
					dependencies.add(line.node_a.id)
			}
		}

		result[node.id] = dependencies
	}

	result.validate()

	return result
}

/**
 * Make all group nodes depend on their children nodes too.
 *
 * Use this one in after toDependencyList() to create a complete dependency list.
 */
fun flattenDependencyList(dependencyList: DependencyList<String>, netlist: NetList) {
	val np = NodeProperties(netlist)
	val netListUtil = NetListUtil(netlist)

	for (parent in netlist.nodes) {
		for (child in netListUtil.getChildren(parent)) {
			// Make group nodes depend on all their children
			dependencyList[parent.id]!!.add(child.id)

			// Make the children nodes depend on the left-connected nodes on the parent
			for (parentInputPort in np.getInputPorts(parent)) {
				val lines = netlist.getConnections(parent, parentInputPort)
				for (line in lines) {
					if (line.node_a.id == parent.id)
						dependencyList[child.id]!!.add(line.node_b.id)
					else
						dependencyList[child.id]!!.add(line.node_a.id)
				}
			}
		}
	}
}