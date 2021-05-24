package net.merayen.elastic.uinodes.list.sin_1

import net.merayen.elastic.backend.logicnodes.list.sin_1.Properties
import net.merayen.elastic.uinodes.list.basemath.BaseMathUI

class UI : BaseMathUI() {
	override val symbol = "sin(x)"
	override val propertiesCls = Properties::class
}