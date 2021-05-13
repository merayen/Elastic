package net.merayen.elastic.system.intercom

/**
 * Data sent in to the processing backend
 *
 * TODO do we need this? Why doesn't NodeDataMessage have nodeId instead?
 */
open class InputFrameData(override val nodeId: String) : NodeDataMessage