package net.merayen.elastic.system;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.context.BackendContext;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.ui.Supervisor;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.Postmaster.Message;
import net.merayen.elastic.util.tap.Tap;
import net.merayen.elastic.util.tap.TapSpreader;

/**
 * This class binds together the backend and the UI.
 * This is the top class for everything.
 */
public class ElasticSystem {
	public interface IListener {
		public void onMessageToUI(Postmaster.Message message);
		public void onMessageToBackend(Postmaster.Message message);
	}

	Supervisor ui;
	BackendContext backend;
	final Environment env;
	private boolean running;
	private final TapSpreader<Postmaster.Message> from_ui = new TapSpreader<>();
	private final TapSpreader<Postmaster.Message> from_backend = new TapSpreader<>();

	public ElasticSystem(Environment env) {
		this.env = env;
	}

	private void start() {
		if(running)
			throw new RuntimeException("Already running");

		backend = BackendContext.create(env); // Start blank, for now. Need some default file
		ui = new Supervisor(new Supervisor.Handler() {

			@Override
			public void onMessageToBackend(Message message) { // Note! This is called from the UI-thread
				backend.message_handler.handleFromUI(message);
				from_backend.push(message);
			}

			@Override
			public void onReadyForMessages() { // Called by UI when it is ready to receive new messages from backend
				for(Postmaster.Message message : backend.message_handler.receiveMessagesFromBackend()) {
					ui.sendMessageToUI(message);
					from_ui.push(message);
				}
			}
		});

		env.synchronization.start();
		running = true;
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		if(!running)
			throw new RuntimeException("Not running");

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
		if(!running)
			return;

		ui.end();
		backend.end();
		env.synchronization.end();

		ui = null;
		backend = null;
		running = false;
	}

	public void restart() {
		end();
		start();
	}

	/**
	* Send message to UI.
	*/
	public void sendMessageToUI(Postmaster.Message message) {
		if(!running)
			throw new RuntimeException("Not running");

		ui.sendMessageToUI(message);
	}

	/**
	* Only to be called outside the ElasticSystem, for testing, or for other control of it.
	*/
	public void sendMessageToBackend(Postmaster.Message message) {
		if(!running)
			throw new RuntimeException("Not running");

		backend.message_handler.handleFromUI(message);
	}

	public Tap<Postmaster.Message> tapIntoMessagesFromUI() {
		return from_ui.create();
	}

	public Tap<Postmaster.Message> tapIntoMessagesFromBackend() {
		return from_backend.create();
	}
}
