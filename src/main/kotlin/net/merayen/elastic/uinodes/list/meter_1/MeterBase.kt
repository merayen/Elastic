package net.merayen.elastic.uinodes.list.meter_1

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

abstract class MeterBase : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f
	var minValue = 1f
	var maxValue = 1f
	var value = 0f
}