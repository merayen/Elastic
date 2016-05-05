package net.merayen.elastic.system;

import net.merayen.elastic.backend.context.BackendContext;
import net.merayen.elastic.ui.Supervisor;
import net.merayen.elastic.util.Postmaster;

/**
 * This class binds together the backend and the UI.
 * This is the top class for everything.
 */
public class ElasticSystem {
	Supervisor uicontext;
	BackendContext backendcontext;

	public ElasticSystem() {
		backendcontext = BackendContext.create(); // Start blank, for now. Need some default file
		uicontext = new Supervisor();
	}

	/**
	 * Needs to be called often.
	 */
	public void update() {
		routeMessages();
	}

	public void end() {
		uicontext.end();
		backendcontext.end();
	}

	private void routeMessages() {
		Postmaster.Message message = backendcontext.receiveFromBackend();
		if(message != null)
			uicontext.sendMessageToUI(message);

		message = uicontext.receiveMessageFromUI();
		if(message != null)
			backendcontext.executeMessage(message);
	}

	/**
	* Only to be called outside the ElasticSystem, for testing, or for other control of it.
	*/
	void sendMessageToUI(Postmaster.Message message) {
		uicontext.sendMessageToUI(message);
	}

	/**
	* Only to be called outside the ElasticSystem, for testing, or for other control of it.
	*/
	void sendMessageToBackend(Postmaster.Message message) {
		backendcontext.executeMessage(message);
	}
}
