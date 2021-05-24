package net.merayen.elastic.uinodes.list.clamp_1

import net.merayen.elastic.backend.logicnodes.list.clamp_1.Properties
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.uinodes.list.basemath.BaseMathUI

class UI : BaseMathUI() {
	override val symbol = "clamp"
	override val propertiesCls = Properties::class

	override fun onPortDisplayName(port: UIPort) = when(port.name) {
		"in0" -> "min"
		"in1" -> "in"
		"in2" -> "max"
		else -> error("Unknown clamp port")
	}
}