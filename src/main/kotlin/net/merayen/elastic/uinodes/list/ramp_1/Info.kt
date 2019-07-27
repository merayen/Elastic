package net.merayen.elastic.uinodes.list.ramp_1

import net.merayen.elastic.uinodes.BaseInfo

class Info : BaseInfo {
	override fun getName() = "Ramp"
	override fun getDescription() = "Converts level to non-linear levels"
	override fun getCategories() = arrayOf("Convert")
}