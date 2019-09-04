package net.merayen.elastic.backend.logicnodes.list.metronome_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

/**
 * @param division E.g 4, means 4 beats in 1 bar
 */
data class Properties(
	var division: Int? = null
) : BaseNodeProperties()