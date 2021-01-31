package net.merayen.elastic.backend.analyzer.node_dependency

/**
 * Simple represention of nodes for analyzing.
 * Format: Map<node, Set<nodes connected to inputs of this node>>
 */
open class DependencyList<T> : HashMap<T, HashSet<T>>() {
	class Invalid(message: String) : RuntimeException(message)

	/**
	 * Returns every node depending on this node directly and indirectly.
	 */
	fun getAllDependents(node: T): Set<T> {
		val result = HashSet<T>()

		val toCheck = HashSet<T>()
		toCheck.add(node)

		while (toCheck.isNotEmpty()) {
			val current = toCheck.first()
			toCheck.remove(current)

			// Add nodes to check if they are not already in the result list
			val toAddToCheck = HashSet<T>(get(current)!!)
			toAddToCheck.removeAll(result)
			toCheck.addAll(toAddToCheck)
			result.addAll(toAddToCheck)
		}

		return result
	}

	/**
	 * Check if there are any cyclic dependencies in the current list of nodes.
	 */
	fun hasCyclicDependencies() = keys.any { it in getAllDependents(it) }

	/**
	 * Retrieve nodes that are sources.
	 * Those are nodes with no inputs but does have outputs.
	 */
	fun getSources() = filter { a -> a.value.isEmpty() && values.any { b -> a.key in b } }.map { it.key }.toSet()

	/**
	 * Retrieve nodes that are targets.
	 * Those are the nodes with inputs but no outputs.
	 */
	fun getTargets(): Set<T> {
		val result = HashSet(keys)

		// For each node, remove what it depends on from the result
		for (dependencies in values)
			result.removeAll(dependencies)

		result.removeIf { this[it]!!.isEmpty() }

		return result
	}

	/**
	 * Retrieve all nodes that does not depend on other nodes and other nodes not depending on it.
	 */
	fun getIndependent(): Set<T> {
		val result = HashSet<T>()

		for ((node, dependencies) in this)
			if (dependencies.isEmpty()) // Node depends on no other nodes
				if (!values.any { node in it })
					result.add(node) // Node depends on no-one and no one depends on it

		return result
	}

	/**
	 * Get all nodes that has more than 1 output line connected.
	 */
	fun getForks(): Set<T> {
		val counts = HashMap(keys.map { it to 0 }.toMap())

		for (nodeId in keys)
			for (dependencies in values)
				if (nodeId in dependencies)
					counts[nodeId] = counts[nodeId]!! + 1

		return counts.filter { it.value > 1 }.map { it.key }.toSet()
	}

	/**
	 * Get all nodes that have more than 1 input line connected from 2 or more different nodes.
	 * This means the node is merging data from 2 or more nodes (like thread joining).
	 */
	fun getJoins() = filter { it.value.size > 1 }.map { it.key }.toSet()

	/**
	 * Get all nodes that does not join or fork (one or less inputs, and one or less outputs)
	 */
	fun getSerials() = keys - getForks() - getJoins()

	/**
	 * Get nodes depending on this node.
	 */
	fun getDependents(node: T) = filter { node in it.value }.map { it.key }.toSet()

	/**
	 * Validate the dependency list for errors.
	 * @throws Invalid
	 */
	fun validate() {
		val invalidDependencies = values.flatten().filter { it !in keys }

		if (invalidDependencies.isNotEmpty())
			throw Invalid("Dependencies not in keys: ${invalidDependencies.joinToString()}")
	}

	inner class NodeInfo(
		val dependencies: HashSet<T>, // left side
		val dependents: Set<T>, // right side
	) {
		val isForking = dependents.size > 1
		val isJoining = dependencies.size > 1
		val isSerial = !isForking && !isJoining
	}

	fun getInfo(node: T) = NodeInfo(get(node)!!, getDependents(node))

	fun getInfo() = this.map { it.key to getInfo(it.key) }.toMap()

	/**
	 * Walk through all the nodes.
	 *
	 * 1. Starts at all the source nodes and follows their paths until target nodes
	 * 2. Then iterates through all the nodes not connected to anyone
	 *
	 * Not thread safe, and does not support concurrent changes while using the iterator.
	 *
	 * Can walk over the same nodes multiple times if the node is a joining node.
	 */
	fun walk(): List<Pair<T, List<T>>> {
		if (hasCyclicDependencies())
			throw RuntimeException("Circular dependencies not supported")

		val result = ArrayList<Pair<T, List<T>>>()
		val walked = ArrayList<T>()

		for (node in getSources())
			walkItem(arrayListOf(node), result, walked)

		for (node in getIndependent())
			result.add(Pair(node, listOf()))

		return result
	}

	private fun walkItem(path: List<T>, result: ArrayList<Pair<T, List<T>>>, walked: ArrayList<T>) {
		val current = path.last()
		walked.add(current)
		result.add(Pair(current, path))
		for (output in getDependents(current)) {
			if (output !in walked) {
				val newPath = ArrayList(path)
				newPath.add(output)
				walkItem(newPath, result, walked)
			}
		}
	}

	override fun clone() = map { it.key to it.value.clone() }
}
