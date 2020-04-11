package net.merayen.elastic.backend.architectures.local.nodes.math_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet
import net.merayen.elastic.backend.architectures.local.lets.SignalOutlet
import net.merayen.elastic.backend.logicnodes.list.math_1.Mode

class LProcessor : LocalProcessor() {
	private var aValue = 0f
	private var bValue = 0f

	override fun onProcess() {
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
				Mode.MODULO -> TODO()
				Mode.LOG -> TODO()
				Mode.SIN -> TODO()
				Mode.COS -> TODO()
				Mode.TAN -> TODO()
				Mode.ASIN -> TODO()
				Mode.ACOS -> TODO()
				Mode.ATAN -> TODO()
				Mode.POWER -> TODO()
			}

		getOutlet("out").push()
	}

	override fun onPrepare() {
		val lnode = localNode as LNode
		aValue = lnode.aValue
		bValue = lnode.bValue
	}

	override fun onInit() {}
	override fun onDestroy() {}
}