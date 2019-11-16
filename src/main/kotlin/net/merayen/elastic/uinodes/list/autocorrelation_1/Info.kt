package net.merayen.elastic.uinodes.list.autocorrelation_1

import net.merayen.elastic.uinodes.BaseInfo

class Info : BaseInfo {
	override fun getName() = "Auto-correlation"
	override fun getDescription() = "Figures out distance between two audio signals by looking for patterns"
	override fun getCategories() = arrayOf("Audio")
}