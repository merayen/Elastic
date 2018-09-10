package net.merayen.elastic.backend.script.interpreter

class Environment(val arrayHeapSizeRestriction: Int = 1024) {
	val variables = HashMap<String,FloatArray>()

	fun memoryToString(): String {
		var result = ""
		for (array in variables) {
			result += array.key.padEnd(10)
			var i = 0
			for (f in array.value) {
				result += i++.toString().padStart(5) + ":" + f.toString().padEnd(10)
			}
			result += "\n"
		}

		return result
	}
}