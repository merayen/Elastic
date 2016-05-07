package net.merayen.elastic.system;

import net.merayen.elastic.backend.context.BackendContext;
import net.merayen.elastic.ui.Supervisor;
import net.merayen.elastic.util.Postmaster;

/**
 * This class binds together the backend and the UI.
 * This is the top class for everything.
 */
public class ElasticSystem {
	Supervisor ui;
	BackendContext backend;

	public ElasticSystem() {
		backend = BackendContext.create(); // Start blank, for now. Need some default file
		ui = new Supervisor();
	}

	/**
	 * Needs to be called often.
	 */
	public void update() {
		routeMessages();
	}

	public void end() {
		ui.end();
		backend.end();
	}

	private void routeMessages() {
		Postmaster.Message message = backend.receiveFromBackend();
		if(message != null)
			ui.sendMessageToUI(message);

		message = ui.receiveMessageFromUI();
		if(message != null)
			backend.executeMessage(message);
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
}
