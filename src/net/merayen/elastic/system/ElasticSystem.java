package net.merayen.elastic.system;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.context.BackendContext;
import net.merayen.elastic.ui.Supervisor;
import net.merayen.elastic.util.Postmaster;

/**
 * This class binds together the backend and the UI.
 * This is the top class for everything.
 */
public class ElasticSystem {
	public interface IListener {
		public void onMessageToUI(Postmaster.Message message);
		public void onMessageToBackend(Postmaster.Message message);
	}

	private IListener listener;

	Supervisor ui;
	BackendContext backend;

	public ElasticSystem() {
		backend = BackendContext.create(); // Start blank, for now. Need some default file
		ui = new Supervisor();
	}

	public static ElasticSystem load(JSONObject dump) {
		ElasticSystem es = new ElasticSystem();

		// TODO send messages to backend

		return es;
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("ui.java", ui.dump());
		result.put("backend", backend.dump());
		return result;
	}

	/**
	 * Needs to be called often.
	 */
	public void update() { // TODO perhaps don't do this, but rather trigger on events
		long t = System.currentTimeMillis() + 10;

		while(t >= System.currentTimeMillis() && routeMessages());
	}

	public void end() {
		ui.end();
		backend.end();
	}

	private boolean routeMessages() {
		boolean activity = false;

		Postmaster.Message message = backend.receiveFromBackend();
		if(message != null) {
			//System.out.printf("Message to UI: %s\n", message);
			ui.sendMessageToUI(message);

			if(listener != null)
				listener.onMessageToUI(message);

			activity = true;
		}

		message = ui.receiveMessageFromUI();
		if(message != null) {
			//System.out.printf("Message from UI: %s\n", message);
			backend.executeMessage(message);

			if(listener != null)
				listener.onMessageToBackend(message);

			activity = true;
		}

		return activity;
	}

	/**
	* Send message to UI.
	*/
	void sendMessageToUI(Postmaster.Message message) {
		ui.sendMessageToUI(message);
	}

	/**
	* Only to be called outside the ElasticSystem, for testing, or for other control of it.
	*/
	void sendMessageToBackend(Postmaster.Message message) {
		backend.executeMessage(message);
	}

	public void listen(IListener listener) {
		this.listener = listener;
	}
}
