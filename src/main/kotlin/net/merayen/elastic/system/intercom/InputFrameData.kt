package net.merayen.elastic.system.intercom

/**
 * Data sent in to the processing backend
 */
open class InputFrameData(override val nodeId: String) : NodeDataMessage