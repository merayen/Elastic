package net.merayen.elastic.system;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.context.BackendContext;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.ui.Supervisor;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.Postmaster.Message;

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

	public ElasticSystem(Environment env) {
		backend = BackendContext.create(env); // Start blank, for now. Need some default file
		ui = new Supervisor(new Supervisor.Handler() {

			@Override
			public void onMessageToBackend(Message message) { // Note! This is called from the UI-thread
				backend.message_handler.handleFromUI(message);

				if(listener != null)
					listener.onMessageToBackend(message);
			}

			@Override
			public void onReadyForMessages() { // Called by UI when it is ready to receive new messages from backend
				for(Postmaster.Message message : backend.message_handler.receiveMessagesFromBackend()) {
					ui.sendMessageToUI(message);

					if(listener != null)
						listener.onMessageToUI(message);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("ui.java", ui.dump());
		result.put("backend", backend.dump());
		return result;
	}

	/**
	 * Needs to be called often by main thread.
	 */
	public void update() { // TODO perhaps don't do this, but rather trigger on events
		backend.update();
	}

	public void end() {
		ui.end();
		backend.end();
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
		backend.message_handler.handleFromUI(message);
	}

	public void listen(IListener listener) {
		this.listener = listener;
	}
}
