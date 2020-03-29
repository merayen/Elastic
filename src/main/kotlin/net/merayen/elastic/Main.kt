package net.merayen.elastic

object Main {
	@JvmStatic
	fun main(args: Array<String>) {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("-agentlib:jdwp") > 0) {
			Config.ui.debug.messages = true
			Config.ui.debug.overlay = true
		}

		if (args.isEmpty()) {
			net.merayen.elastic.system.Test.test()
		} else {
			when (args[0]) {
				"run" -> net.merayen.elastic.system.Test.test()
				else -> println("Unknown command")
			}
		}
	}
}
