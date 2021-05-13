package net.merayen.elastic.backend.logicnodes.list.meter_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.uinodes.list.meter_1.CircleMeter
import net.merayen.elastic.uinodes.list.meter_1.MeterBase
import net.merayen.elastic.uinodes.list.meter_1.RowMeter
import kotlin.reflect.KClass

class Properties(
	var minValue: Float? = null,
	var maxValue: Float? = null,
	var auto: Boolean? = null,
	var meterType: String? = null,
) : BaseNodeProperties() {
	enum class MeterType(val cls: KClass<out MeterBase>) {
		ROW(RowMeter::class),
		CIRCLE(CircleMeter::class),
	}
}