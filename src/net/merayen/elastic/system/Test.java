package net.merayen.elastic.system;

import net.merayen.elastic.system.intercom.*;

public class Test {
	public static void test() {
		ElasticSystem system = new ElasticSystem();
		//system.sendMessageToUI(new DummyMessage());
		//system.sendMessageToBackend(new DummyMessage());
		//system.sendMessageToBackend(new CreateNodeMessage("group", 1));

		try {
			Thread.sleep(1000); // Give some time for the UI to create the viewports and the NodeView
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		system.sendMessageToBackend(new CreateNodeMessage("test", 100));

		long t = System.currentTimeMillis() + 600000000;
		while(t > System.currentTimeMillis()) {
			system.update();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		system.end();
	}
}
