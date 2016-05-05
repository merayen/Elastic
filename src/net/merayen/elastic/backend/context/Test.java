package net.merayen.elastic.backend.context;

public class Test {
	public static void test() {
		BackendContext context = BackendContext.create();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		context.end();
	}
}
