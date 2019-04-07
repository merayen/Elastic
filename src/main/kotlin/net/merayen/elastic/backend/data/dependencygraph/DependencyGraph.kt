package net.merayen.elastic.backend.data.dependencygraph

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

/**
 * Keeps track of resources and their dependencies.
 * Note: It is not possible to directly delete dependencygraph. You will need to clear dependencies
 * to have the function tidy() to actually delete resources. This is by design.
 */
class DependencyGraph {
    internal val list: MutableMap<String, DependencyItem> = HashMap()

    val top: DependencyItem
        get() = list[""] ?: throw RuntimeException("Should not happen")

    val all: Map<String, DependencyItem>
        @Synchronized get() = HashMap(list)

    private val active: Set<DependencyItem>
        get() {
            val active = HashSet<DependencyItem>()
            val toCheck = ArrayList<DependencyItem>()
            active.add(top)
            toCheck.add(top)

            while (!toCheck.isEmpty()) {
                for (r in toCheck.removeAt(0).dependsOn) {
                    if (!active.contains(r) && !toCheck.contains(r)) {
                        toCheck.add(r)
                        active.add(r)
                    }
                }
            }

            return active
        }

    init {
        create("") // Create "top"-object that is permanent. Never to be deleted
    }

    /**
     * Creates a new DependencyItem.
     * @param id Unique id of the new DependencyItem
     * @throws RuntimeException if id already exists
     */
    @Synchronized
    fun create(id: String): DependencyItem {
        if (list.containsKey(id))
            throw RuntimeException("DependencyItem already exists")

        val r = DependencyItem(id)

        list[r.id] = r

        return r
    }

    @Synchronized
    operator fun get(id: String): DependencyItem? {
        return list[id]
    }

    /**
     * Deletes resources that has no dependencies to itself.
     * Not tested.
     */
    @Synchronized
    fun tidy() {
        val active = active

        list.entries.removeIf { x -> !active.contains(x.value) }
    }
}
