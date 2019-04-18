package net.merayen.elastic.system;

import net.merayen.elastic.backend.context.BackendContext;
import net.merayen.elastic.system.intercom.backend.EndBackendMessage;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.system.intercom.ui.EndUIMessage;
import net.merayen.elastic.system.intercom.ui.InitUIMessage;
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
	static {
		//System.load("/usr/lib/x86_64-linux-gnu/libffms2.so.4");
	}

	public interface IListener {
		void onMessageToUI(Postmaster.Message message);
		void onMessageToBackend(Postmaster.Message message);
	}

	Supervisor ui;
	volatile BackendContext backend;
	private final TapSpreader<Postmaster.Message> from_ui = new TapSpreader<>();
	private final TapSpreader<Postmaster.Message> from_backend = new TapSpreader<>();

	/**
	 * Needs to be called often by main thread.
	 */
	public void update() { // TODO perhaps don't do this, but rather trigger on events
		if(backend != null) {
			backend.update();
			if(ui == null) // UI is not present, we retrieve messages from the backend, as UI usually does this
				for(Postmaster.Message message : backend.message_handler.receiveMessagesFromBackend())
					from_backend.push(message);
		}
	}

	public void end() {
		if(ui != null)
			ui.end();

		if(backend != null)
			backend.end();

		ui = null;
		backend = null;
	}

	/**
	* Send message to UI.
	*/
	public void sendMessageToUI(Postmaster.Message message) {
		if(ui == null && message instanceof InitUIMessage) {
			ui = new Supervisor(new Supervisor.Handler() {
				@Override
				public void onMessageToBackend(Message message) { // Note! This is called from the UI-thread
					if(backend != null)
						backend.message_handler.sendToBackend(message);

					from_ui.push(message);
				}

				@Override
				public void onReadyForMessages() { // Called by UI when it is ready to receive new messages from backend
					if(backend != null) {
						for(Postmaster.Message message : backend.message_handler.receiveMessagesFromBackend()) {
							ui.sendMessageToUI(message);
							from_backend.push(message);
						}
					}
				}
			}
			);
		}

		if(ui != null && message instanceof EndUIMessage) {
			ui.end();
			ui = null;
		}

		if(ui != null)
			ui.sendMessageToUI(message);
	}

	/**
	* Only to be called outside the ElasticSystem, for testing, or for other control of it.
	*/
	public synchronized void sendMessageToBackend(Postmaster.Message message) {
		if(message instanceof InitBackendMessage) {
			if(backend == null) {
				backend = new BackendContext(this, (InitBackendMessage)message);
			}
		} else if(backend == null) {
			System.out.println("Ignoring message as backend is not running: " + message);
			return;
		}

		if(message instanceof EndBackendMessage) {
			backend.end();
			backend = null;
		}

		backend.message_handler.sendToBackend(message);
	}

	public Tap<Postmaster.Message> tapIntoMessagesFromUI() {
		return from_ui.create();
	}

	public Tap<Postmaster.Message> tapIntoMessagesFromBackend() {
		return from_backend.create();
	}

	public void runAction(Action action) {
		action.start(this);
	}
}
