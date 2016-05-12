package net.merayen.elastic.ui.controller;

import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.intercom.ViewportHelloMessage;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.util.Postmaster.Message;

public class ViewportController extends Controller {
	ViewportContainer viewport_container;

	public ViewportController(Gate gate) {
		super(gate);
	}

	@Override
	protected void onInit() {}

	@Override
	public void onMessageFromBackend(Message message) {
		// Views are internal for the UI, no interaction with backend here
	}

	@Override
	public void onMessageFromUI(Message message) {
		if(message instanceof ViewportHelloMessage) // Received from ViewportContainer UIObject when it has inited. We can then manage it.
			viewport_container = ((ViewportHelloMessage)message).viewport_container;
	}

	@Override
	protected JSONObject onDump() {
		return viewport_container.dump();
	}

	@Override
	protected void onRestore(JSONObject obj) {
		if(viewport_container == null)
			throw new RuntimeException("Should not happen");

		viewport_container.restore(obj);
	}
}
