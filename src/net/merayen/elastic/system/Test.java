package net.merayen.elastic.system;

import java.util.ArrayList;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster.Message;

public class Test {
	public static void test() {
		new Test();
	}

	private final ElasticSystem system;

	public Test() {
		system = new ElasticSystem();

		try {
			Thread.sleep(1000); // Give some time for the UI to create the viewports and the NodeView
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ArrayList<NodeCreatedMessage> nodes = new ArrayList<>();
		system.listen(new ElasticSystem.IListener() {
			@Override
			public void onMessageToUI(Message message) {
				if(message instanceof NodeCreatedMessage) {
					nodes.add((NodeCreatedMessage)message);
				}
			}

			@Override
			public void onMessageToBackend(Message message) {
				
			}
		});

		system.sendMessageToBackend(new CreateNodeMessage("test", 100));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1));
		system.sendMessageToBackend(new CreateNodeMessage("output", 1));

		operate(100, 10);

		// Place the nodes
		system.sendMessageToUI(new NodeParameterMessage(nodes.get(1).node_id, "ui.java.translation.x", 50f));
		system.sendMessageToUI(new NodeParameterMessage(nodes.get(2).node_id, "ui.java.translation.x", 200f));

		// Connect signal generator to output
		system.sendMessageToBackend(new NodeConnectMessage(nodes.get(1).node_id, "output", nodes.get(2).node_id, "input"));
		//system.sendMessageToBackend(new NodeConnectMessage("signalgenerator", 1));

		operate(10000000, 1000);

		system.end();
	}

	private void operate(long ms, long sleep) {
		long t = System.currentTimeMillis() + ms;
		while(t > System.currentTimeMillis()) {
			system.update();

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
