package net.merayen.elastic.uinodes.list.from_midi_1

import net.merayen.elastic.uinodes.BaseInfo

class Info : BaseInfo {
	override fun getName() = "From Midi"
	override fun getDescription() = "Convert midi to discrete signals"
	override fun getCategories() = arrayOf("core")
}