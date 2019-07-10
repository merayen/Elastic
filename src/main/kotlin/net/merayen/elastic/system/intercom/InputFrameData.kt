package net.merayen.elastic.system.intercom

/**
 * Data sent in to the processing backend
 */
class InputFrameData(override val nodeId: String, data: Map<String, Any>) : NodeDataMessage