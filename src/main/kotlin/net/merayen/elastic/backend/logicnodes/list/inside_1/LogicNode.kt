package net.merayen.elastic.backend.logicnodes.list.inside_1

import net.merayen.elastic.backend.logicnodes.list.basemath.BaseMathLogicNode

/**
 * Outputs 1.0 if value is between set range.
 */
class LogicNode : BaseMathLogicNode() {
	override val minimumInputCount = 3
	override val maximumInputCount = 3
}