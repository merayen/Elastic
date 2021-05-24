package net.merayen.elastic.util.math

import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow

fun prettyNumber(number: Number, decimalPlaces: Int = 1): String {
	val units = arrayOf("f", "p", "n", "µ", "m", "", "K", "M", "G", "T", "P")
	val center = 5

	val num = number.toDouble()

	if (num == 0.0)
		return "0.0"

	val l = log10(abs(num)).toInt()
	if (l / 3 < -center) return "0.0"
	if (l / 3 >= units.size - center) return "A lot"

	val unitIndex = max(0, min(units.size - 1, l / 3 + center))

	return "${if (num < 0) "-" else ""}${"%.${decimalPlaces}f".format(num / 10.0.pow((l / 3) * 3))}${units[unitIndex]}"
}