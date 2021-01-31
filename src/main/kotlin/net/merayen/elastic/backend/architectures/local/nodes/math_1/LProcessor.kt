package net.merayen.elastic.backend.architectures.local.nodes.math_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalOutlet
import net.merayen.elastic.backend.logicnodes.list.math_1.Mode
import kotlin.math.*

class LProcessor : LocalProcessor() {
	private var aValue = 0f
	private var bValue = 0f
	private var done = false

	override fun onProcess() {
		if (done || !available())
			return

		val aBuffer = (getInlet("a") as? SignalInlet)?.outlet?.signal
		val bBuffer = (getInlet("b") as? SignalInlet)?.outlet?.signal
		val out = (getOutlet("out") as? SignalOutlet)?.signal ?: return

		val mode = (localNode as LNode).mode

		for (i in 0 until buffer_size)  // This code is made to be beautiful to look at. It might give crap performance
			out[i] = when (mode) {
				Mode.ADD -> (aBuffer?.get(i) ?: aValue) + (bBuffer?.get(i) ?: bValue)
				Mode.SUBTRACT -> (aBuffer?.get(i) ?: aValue) - (bBuffer?.get(i) ?: bValue)
				Mode.MULTIPLY -> (aBuffer?.get(i) ?: aValue) * (bBuffer?.get(i) ?: bValue)
				Mode.DIVIDE -> (aBuffer?.get(i) ?: aValue) / (bBuffer?.get(i) ?: bValue)
				Mode.MODULO -> (aBuffer?.get(i) ?: aValue) % (bBuffer?.get(i) ?: bValue)
				Mode.LOG -> log(aBuffer?.get(i) ?: aValue, max(1.001f, bBuffer?.get(i) ?: bValue))
				Mode.POWER -> (aBuffer?.get(i) ?: aValue).pow((bBuffer?.get(i) ?: bValue))
				Mode.SIN -> sin(aBuffer?.get(i) ?: aValue)
				Mode.COS -> cos(aBuffer?.get(i) ?: aValue)
				Mode.TAN -> tan(aBuffer?.get(i) ?: aValue)
				Mode.ASIN -> asin(aBuffer?.get(i) ?: aValue)
				Mode.ACOS -> acos(aBuffer?.get(i) ?: aValue)
				Mode.ATAN -> atan(aBuffer?.get(i) ?: aValue)
			}

		getOutlet("out").push()
		done = true
	}

	override fun onPrepare() {
		val lnode = localNode as LNode
		aValue = lnode.aValue
		bValue = lnode.bValue
		done = false
	}

	override fun onInit() {}
	override fun onDestroy() {}
}