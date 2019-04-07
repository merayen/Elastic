package net.merayen.elastic.uinodes.list.eq_1

class MultiParameterEqData {
	class EqPoint(var frequency: Float, var q: Float = 1f, var amplitude: Float = 1f)

	val points = ArrayList<EqPoint>()
}