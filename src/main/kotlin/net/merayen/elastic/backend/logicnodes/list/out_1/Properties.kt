package net.merayen.elastic.backend.logicnodes.list.out_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

/**
 * @param portName Name of the port. Is used by the parent node for identification, e.g forward data from out-node
 */
class Properties(var portName: String? = null) : BaseNodeProperties()