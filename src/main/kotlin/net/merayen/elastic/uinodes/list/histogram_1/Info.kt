package net.merayen.elastic.uinodes.list.histogram_1

import net.merayen.elastic.uinodes.BaseInfo

class Info : BaseInfo {
	override fun getName() = "Histogram"
	override fun getDescription() = "A histogram of the data/audio"
	override fun getCategories() = arrayOf("Analyze")
}