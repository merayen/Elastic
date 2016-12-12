package net.merayen.elastic.ui;

import net.merayen.elastic.util.Postmaster.Message;

public class Test {
	public static void test() {
		Supervisor supervisor = new Supervisor(new Supervisor.Handler() {

			@Override
			public void onMessageToBackend(Message message) {
				// Meh
			}
		});
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		supervisor.end();
	}
}
