package net.merayen.elastic

object Main {
	@JvmStatic
	fun main(args: Array<String>) {
		if (args.isEmpty()) {
			net.merayen.elastic.system.Test.test()
		} else {
			when(args[0]) {
				"run" -> net.merayen.elastic.system.Test.test()
				"highland" -> net.merayen.elastic.backend.script.highlang.main(args.sliceArray(IntRange(1, args.size - 1)))
				else -> println("Unknown command")
			}
		}
	}
}
