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

	public void load(JSONObject dump) {
		
	}

	public JSONObject dump() {
		return null; // TODO
	}

	/**
	 * Needs to be called often.
	 */
	public void update() {
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
			ui.sendMessageToUI(message);

			if(listener != null)
				listener.onMessageToUI(message);

			activity = true;
		}

		message = ui.receiveMessageFromUI();
		if(message != null) {
			backend.executeMessage(message);

			if(listener != null)
				listener.onMessageToBackend(message);

			activity = true;
		}

		return activity;
	}

	/**
	* Only to be called outside the ElasticSystem, for testing, or for other control of it.
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
