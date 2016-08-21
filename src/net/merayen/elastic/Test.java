package net.merayen.elastic;

public class Test {

	public static void main(String[] args) {
		java勉強();
		net.merayen.elastic.backend.buffer.FloatCircularBuffer.test();
		//net.merayen.elastic.buffer.AudioCircularBuffer.test();
		net.merayen.elastic.netlist.Test.test();
		net.merayen.elastic.backend.architectures.local.Test.test();
		//net.merayen.elastic.backend.nodes.Test.test();
		//net.merayen.elastic.backend.architectures.Test.test();
		//net.merayen.elastic.backend.context.Test.test();
		net.merayen.elastic.backend.analyzer.Test.test();
		net.merayen.elastic.ui.objects.top.viewport.Test.test();
		net.merayen.elastic.system.Test.test();
		//net.merayen.elastic.ui.Test.test();

		System.out.println("Test done");
	}

	private static void java勉強() {
		
	}

	/*private static int map(Runnable func, int[] numbers) {
		int i = 0;
		for(int n : numbers)
			func.run();
	}*/
}
