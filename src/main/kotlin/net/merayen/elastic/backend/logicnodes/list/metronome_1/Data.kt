package net.merayen.elastic.backend.logicnodes.list.metronome_1

import net.merayen.elastic.backend.nodes.BaseNodeData

/**
 * @param division E.g 4, means 4 beats in 1 bar
 */
data class Data(
	var division: Int? = null
) : BaseNodeData()