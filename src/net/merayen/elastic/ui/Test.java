package net.merayen.elastic.ui;

public class Test {
	public static void test() {
		Supervisor supervisor = new Supervisor();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		supervisor.end();
	}
}
