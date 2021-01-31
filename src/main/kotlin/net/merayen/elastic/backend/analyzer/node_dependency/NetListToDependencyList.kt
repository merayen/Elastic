package net.merayen.elastic.backend.analyzer.node_dependency

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.netlist.NetList

/**
 * Creates a NodeList from a NetList.
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
 * Flatten a DependencyList by making all the dependents on a group node to depend on its children nodes too.
 */
fun flattenDependencyList(dependencyList: DependencyList<String>, netlist: NetList) {
	val np = NodeProperties(netlist)
	val nodesByParent = HashMap<String, HashSet<String>>()

	// Arrange nodes after their parent
	for (node in netlist.nodes) {
		val parent = np.getParent(node) ?: continue

		if (parent !in nodesByParent)
			nodesByParent[parent] = HashSet()

		nodesByParent[parent]!!.add(node.id)
	}

	// Iterate over parents and set their children to depend on the nodes the parent depends on
	// Comment: Maybe we could only set some of them to depend on parents dependencies? What about loops?
	for ((parent, children) in nodesByParent) {
		val parentDependencies = dependencyList[parent]!!

		for (parentDependency in parentDependencies)
			for (child in children)
				dependencyList[child]!!.add(parentDependency)
	}

	// Find all nodes that depends on this parent and make them depend on all the nodes inside the parent
	for ((parent, children) in nodesByParent)
		for (dependencies in dependencyList.values)
			if (parent in dependencies)
				for (child in children)  // Really? 3 nested for-loops? No? Or?
					dependencies.add(child)

	dependencyList.validate()
}
