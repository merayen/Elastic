package net.merayen.elastic.system.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.merayen.elastic.system.Action;
import net.merayen.elastic.system.intercom.CreateNodeMessage;
import net.merayen.elastic.system.intercom.NodeConnectMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.ResetNetListMessage;
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.system.intercom.ui.InitUIMessage;
import net.merayen.elastic.util.tap.Tap;
import net.merayen.elastic.util.tap.TapSpreader;
import net.merayen.elastic.util.Postmaster;

/**
 * Creates a new blank project, with a few nodes.
 */
public class NewProject extends Action {
	ArrayList<CreateNodeMessage> nodes = new ArrayList<>();
	private final String path;

	public NewProject(String path) {
		if(new File(path).exists())
			throw new RuntimeException("Project already exists. Use LoadProject()-action instead.");
		this.path = path;
	}

	@Override
	protected void run() {
		try(
			Tap<Postmaster.Message> from_backend = system.tapIntoMessagesFromBackend()) {

			from_backend.set(new TapSpreader.Func<Postmaster.Message>() {
				public void receive(Postmaster.Message message) {
					if(message instanceof CreateNodeMessage) {
						nodes.add((CreateNodeMessage)message);
					} else if(message instanceof ResetNetListMessage) {
						nodes.clear();
					} else if(message instanceof ProcessMessage) {
						//ProcessMessage pm = (ProcessMessage)message;
						System.out.printf("Process response\n");
					}
				}
			});

			init();
		}
	}

	private void init() {
		system.end();

		system.sendMessageToUI(new InitUIMessage());

		system.sendMessageToBackend(new InitBackendMessage(44100, 16, 512, path));

		system.sendMessageToBackend(new CreateNodeMessage("test", 100, null));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1, null));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1, null));
		system.sendMessageToBackend(new CreateNodeMessage("output", 1, null));
		system.sendMessageToBackend(new CreateNodeMessage("mix", 1, null));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1, null));

		waitFor(() -> nodes.size() == 6);

		// Place the nodes
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(0).node_id, "ui.java.translation.y", 300f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(1).node_id, "ui.java.translation.x", 400f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(1).node_id, "ui.java.translation.y", 400f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(3).node_id, "ui.java.translation.x", 800f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(3).node_id, "ui.java.translation.y", 400f));

		// Connect signal generator to output
		system.sendMessageToBackend(new NodeConnectMessage(nodes.get(1).node_id, "output", nodes.get(3).node_id, "input"));
		system.sendMessageToBackend(new NodeConnectMessage(nodes.get(2).node_id, "output", nodes.get(1).node_id, "frequency"));

		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(2).node_id, "data.frequency", 1f));

		{long t = System.currentTimeMillis() + 1000; waitFor(() -> t > System.currentTimeMillis());}

		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(1).node_id, "data.InputSignalParameters:frequency", new HashMap<String,Object>(){{
			put("amplitude", 1000f);
			put("offset", 440f*4);
		}}));

		system.sendMessageToBackend(new CreateCheckpointMessage());
	}

	interface Func {
		public boolean noe();
	}

	private void waitFor(Func func) {
		try {
			while(!func.noe()) {
				system.update();
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}