package net.merayen.elastic.uinodes.list.frequency_1

import net.merayen.elastic.uinodes.BaseInfo

class Info : BaseInfo {
	override fun getName() = "Frequency"
	override fun getDescription() = "Analyze frequencies and output midi"
	override fun getCategories() = arrayOf("Audio")
}