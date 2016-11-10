package net.merayen.elastic.system;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster.Message;

public class Test {
	public static void test() {
		new Test();
	}

	private ElasticSystem system;

	private Test() {
		system = new ElasticSystem();

		try {
			Thread.sleep(1000); // Give some time for the UI to create the viewports and the NodeView
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ArrayList<CreateNodeMessage> nodes = new ArrayList<>();

		system.listen(new ElasticSystem.IListener() {
			@Override
			public void onMessageToUI(Message message) {
				if(message instanceof CreateNodeMessage) {
					nodes.add((CreateNodeMessage)message);
				}
			}

			@Override
			public void onMessageToBackend(Message message) {
				
			}
		});

		system.sendMessageToBackend(new CreateNodeMessage("test", 100));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1));
		system.sendMessageToBackend(new CreateNodeMessage("output", 1));

		waitFor(() -> nodes.size() == 4);

		// Place the nodes
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(1).node_id, "ui.java.translation.x", 50f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(3).node_id, "ui.java.translation.x", 200f));

		// Connect signal generator to output
		system.sendMessageToBackend(new NodeConnectMessage(nodes.get(1).node_id, "output", nodes.get(3).node_id, "input"));

		final long t = System.currentTimeMillis() + 1 * 1000;
		waitFor(() -> System.currentTimeMillis() > t);

		// Test dumping
		String dump = system.dump().toJSONString();
		System.out.println(dump);

		/*system.end();

		// Test restoring the dump
		try {
			system = ElasticSystem.load((JSONObject)new org.json.simple.parser.JSONParser().parse(dump));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}*/

		// Now just run
		final long u = System.currentTimeMillis() + 3600 * 1000;

		waitFor(new Func() {
			long s = System.currentTimeMillis();

			@Override
			public boolean noe() {
				if(s + 1000 < System.currentTimeMillis()) {
					s = System.currentTimeMillis();
					system.sendMessageToBackend(new ProcessMessage());
				}
				return System.currentTimeMillis() > u;
			}
		});

		system.end();
	}

	interface Func {
		public boolean noe();
	}

	private void waitFor(Func func) {
		try {
			while(!func.noe()) {
				system.update();
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
