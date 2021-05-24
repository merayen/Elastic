package net.merayen.elastic.backend.logicnodes.list.basemath

import net.merayen.elastic.backend.nodes.BaseNodeProperties

open class BaseMathProperties( // Note, if changing anything, change all the subclasses too :/
	var portValues: List<Float>? = null,
) : BaseNodeProperties()
