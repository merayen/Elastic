package net.merayen.elastic.backend.script.interpreter

import net.merayen.elastic.backend.script.parser.Parser

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

		for (dim in dims)
			environment.arrayVariables[dim.arrayVariableName] = FloatArray(dim.arrayLength.toInt())
	}
}