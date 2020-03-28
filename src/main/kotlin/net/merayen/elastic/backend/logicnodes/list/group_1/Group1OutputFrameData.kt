package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.NodeStatusMessage
import net.merayen.elastic.system.intercom.OutputFrameData

/**
 * @param currentPlayheadPosition Where the current playhead position is at
 */
class Group1OutputFrameData(
	nodeId: String,
	nodeStats: NodeStatusMessage,
	currentPlayheadPosition: Float
) : OutputFrameData(nodeId, nodeStats)