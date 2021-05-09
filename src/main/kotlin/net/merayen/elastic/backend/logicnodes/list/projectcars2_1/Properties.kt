package net.merayen.elastic.backend.logicnodes.list.projectcars2_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

/**
 * @param sourceIP Where we listen for ProjectCars 2 data. If not set, we choose automatically
 */
class Properties(
	var sourceIP: String? = null
): BaseNodeProperties()