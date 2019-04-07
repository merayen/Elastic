package net.merayen.elastic.backend.script.interpreter

import net.merayen.elastic.backend.script.parser.Parser
import java.util.*

class OutOfArrayMemory(used: Int, available: Int) : InterpreterException("Tried to allocate $used memory while only $available is available for arrays")

class Interpreter(parser: Parser) {
	val environment = Environment()
	val programNode = getInterpreterNode(environment, null, parser.result)

	fun run() {
		dimensionArrays()
		programNode.eval()
	}

	/**
	 * Scans the whole program and declares any arrays that has been declared in the program.
	 */
	private fun dimensionArrays() {
		val dims = programNode.find(Dim::class)
		var memoryUsed = dims.sumBy { it.arrayLength.toInt() }

		if(memoryUsed > environment.arrayHeapSizeRestriction)
			throw OutOfArrayMemory(memoryUsed, environment.arrayHeapSizeRestriction)

		val random = Random()
		for (dim in dims) {
			val array = FloatArray(dim.arrayLength.toInt())

			// Scrambles the memory, as no memory is cleared upon running a program. This helps to find bugs where the dev has not inited/written to memory
			for (i in 0 until dim.arrayLength)
				if (random.nextBoolean())
					array[i] = (random.nextDouble() * Float.MAX_VALUE).toFloat()
				else
					array[i] = (random.nextDouble() * Float.MIN_VALUE).toFloat()

			environment.variables[dim.arrayVariableName] = array
		}
	}
}