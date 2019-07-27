package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.uinodes.BaseInfo

class Info : BaseInfo {
	override fun getName() = "Sample"
	override fun getDescription() = "Samples sound"
	override fun getCategories() = arrayOf("Audio")
}