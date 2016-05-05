package net.merayen.elastic.ui.controller;

import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.util.Postmaster;

public abstract class Controller {
	protected Top top;

	public Controller(Top top) {
		this.top = top;
	}

	/**
	 * Message received from the backend.
	 */
	public abstract void onMessageFromBackend(Postmaster.Message message);

	/**
	 * Message sent from the UI.
	 */
	public abstract void onMessageFromUI(Postmaster.Message message);
}
