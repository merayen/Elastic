package net.merayen.elastic.ui.controller;

import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.util.Postmaster;

public abstract class Controller {
	protected Gate gate;

	public Controller(Gate gate) {
		this.gate = gate;
	}

	/**
	 * Message received from the backend.
	 */
	public abstract void onMessageFromBackend(Postmaster.Message message);

	/**
	 * Message sent from the UI.
	 */
	public abstract void onMessageFromUI(Postmaster.Message message);

	public void sendToBackend(Postmaster.Message message) {
		gate.to_backend.send(message);
	}

	public Top getTopObject() {
		return gate.top;
	}
}
