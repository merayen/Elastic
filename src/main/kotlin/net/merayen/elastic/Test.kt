package net.merayen.elastic

class Test {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			// Doesn't work. Fix!
			//net.merayen.elastic.backend.nodes.Test.test();
			//net.merayen.elastic.backend.architectures.Test.test();

			// Works
			//net.merayen.elastic.backend.architectures.local.Test.test();

			//net.merayen.elastic.system.intercom.Test.test();

			//net.merayen.elastic.backend.interfacing.Test.test();

			if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0) {
				Config.ui.debug.messages = true
				Config.ui.debug.overlay = true
			}

			net.merayen.elastic.system.Test.test()
		}
	}
}