package net.merayen.elastic.backend.data.dependencygraph

import java.util.HashMap
import java.util.HashSet

/**
 * Represents a dependencygraph, which can be a audio clip, midi etc.
 * When changing the underlying data, lock this DependencyItem()-instance.
 */
class DependencyItem internal constructor(
        /**
         * Unique ID for this dependencygraph. This is also the path, so id can typically be:
         * "audio/samples/somefile.wav"
         */
        val id: String) {

    /**
     * Resources this dependencygraph depends on.
     * Resources that has no dependencies to themselves will be deleted.
     */
    val dependsOn: Set<DependencyItem> = HashSet()

    /**
     * Key-value properties for the dependencygraph. Free to use. Must be JSON-compatible.
     */
    val data: Map<String, Any> = HashMap()
}