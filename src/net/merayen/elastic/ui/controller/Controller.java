package net.merayen.elastic.ui.controller;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.util.Postmaster;

public abstract class Controller {
	protected Gate gate;

	public Controller(Gate gate) {
		this.gate = gate;
	}

	protected abstract void onInit();

	/**
	 * Message received from the backend.
	 */
	protected abstract void onMessageFromBackend(Postmaster.Message message);

	/**
	 * Message sent from the UI.
	 */
	protected abstract void onMessageFromUI(Postmaster.Message message);

	public void sendToBackend(Postmaster.Message message) {
		gate.to_backend.send(message);
	}

	public Top getTopObject() {
		return gate.top;
	}

	protected abstract JSONObject onDump();
	protected abstract void onRestore(JSONObject obj);
}
