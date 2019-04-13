package net.merayen.elastic

class Test {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			// Doesn't work. Fix!
			//net.merayen.elastic.backend.nodes.Test.test();
			//net.merayen.elastic.backend.architectures.Test.test();
			//net.merayen.elastic.backend.context.Test.test();

			// Works
			//net.merayen.elastic.backend.architectures.local.Test.test();
			//net.merayen.elastic.util.Test.test();

			//net.merayen.elastic.netlist.Test.test();
			//net.merayen.elastic.system.intercom.Test.test();

			//net.merayen.elastic.backend.analyzer.Test.test();
			//net.merayen.elastic.backend.interfacing.Test.test();
			//net.merayen.elastic.backend.mix.Test.test();
			//net.merayen.elastic.ui.objects.top.viewport.Test.test();

			//net.merayen.elastic.backend.architectures.local.nodes.delay_1.Delay.test();

			//net.merayen.elastic.ui.Test.test();

			//net.merayen.elastic.backend.queue.TestKt.test();

			net.merayen.elastic.system.Test.test()

			//println(net.merayen.elastic.system.Test.test())

			println("Test done")
		}
	}
}