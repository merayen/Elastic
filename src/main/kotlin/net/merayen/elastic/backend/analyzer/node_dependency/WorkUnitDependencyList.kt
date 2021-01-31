package net.merayen.elastic.backend.analyzer.node_dependency

import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Groups nodes into parallelizable groups.
 */
class WorkUnitDependencyList<T>(dependencyList: DependencyList<T>) : DependencyList<WorkUnitDependencyList<T>.WorkUnit>() {
	/**
	 * A work unit is 1 unit of work that can be run by a node.
	 * Note that this is added to a DependencyList, so that the WorkUnits need to be executed in a certain order.
	 */
	inner class WorkUnit(val nodes: ArrayList<T> = ArrayList())

	init {
		dependencyList.validate()

		if (dependencyList.hasCyclicDependencies())
			throw RuntimeException("Dependency list has cycles. That is not supported")
	}

	init {
		val units = HashMap<T, WorkUnit>()
		for (node in dependencyList.keys) {
			val workUnit = WorkUnit(arrayListOf(node))
			units[node] = workUnit
			put(workUnit, hashSetOf())
		}

		for ((node, dependencies) in dependencyList) {
			val workUnit = units[node]!!
			get(workUnit)!!.addAll(dependencies.map { units[it]!! })
		}

		validate()
	}

	/**
	 * Collapses serial nodes (inputs < 2, outputs < 2) onto single nodes.
	 */
	fun collapseSerials() {
		val infos = getInfo()
		val remaining = ArrayDeque(keys.filter { (infos[it]!!.dependencies.size == 1 || infos[it]!!.dependents.size == 1) })

		println("Serial nodes to group: ${remaining.map { it.nodes }}")

		val toSerialize = ArrayList<List<WorkUnit>>()

		while (remaining.isNotEmpty()) {
			val middle = remaining.removeLast()

			println("Trying to collapse ${middle.nodes}")

			val toSerializeGroup = ArrayDeque<WorkUnit>()
			toSerializeGroup.add(middle)

			var current = middle

			// Try to expand by following left (follow dependencies)
			while (true) {
				val info = infos[current]!!

				if (info.dependencies.size != 1)
					break

				current = info.dependencies.first()
				val leftInfo = infos[current]!!

				if (current !in remaining || !leftInfo.isSerial)
					break

				println("addFirst(${current.nodes})")
				toSerializeGroup.addFirst(current)
			}

			// Then expand from middle to the right (follow dependents)
			current = middle
			while (true) {
				val info = infos[current]!!

				if (info.dependents.size != 1)
					break

				current = info.dependents.first()
				val rightInfo = infos[current]!!

				if (current !in remaining)
					break

				println("addLast(${current.nodes})")
				toSerializeGroup.addLast(current)

				if (rightInfo.dependents.size != 1)
					break // Don't continue following this node as it stops or forks
			}

			if (toSerializeGroup.size < 2)
				continue

			// We should not check the nodes we walked to
			remaining.removeAll(toSerializeGroup)

			println("Will group ${toSerializeGroup.map { it.nodes }}")

			toSerialize.add(toSerializeGroup)
		}

		for (group in toSerialize) {
			if (group.size < 2) error("Must be at least 2 nodes to collapse into 1 WorkUnit")

			// Create a new one
			val workUnit = WorkUnit()

			// Put all the nodes in the group into out new WorkUnit, in the order group dictates (important)
			workUnit.nodes.addAll(group.map { it.nodes }.flatten())

			// Figure out the dependency
			val dependencies = get(group[0])!!

			//if (dependencies.size > 1) error("All nodes collected previously should only be serial...")

			// Figure who depended on our right-most node
			val dependents = infos[group.last()]!!.dependents

			//if (dependents.size > 1) error("All nodes collected previously should only be serial...")

			for (x in dependents) {
				if (!get(x)!!.remove(group.last())) error("Should have been removable")
				get(x)!!.add(workUnit)
			}

			// Remove all existing WorkUnits
			for (x in group)
				remove(x)

			put(workUnit, dependencies)
		}

		validate()
	}
}