package net.merayen.elastic.system.intercom

/**
 * Data sent from the process backend when finish with a frame.
 * Read by the logic node for further processing/interpretation.
 */
data class OutputFrameData(override val nodeId: String, val data: Map<String, Any>) : NodeDataMessage {}