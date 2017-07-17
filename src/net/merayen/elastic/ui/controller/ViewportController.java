package net.merayen.elastic.ui.controller;

import org.json.simple.JSONObject;

import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.ui.intercom.ViewportHelloMessage;
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
		if(message instanceof NetListRefreshRequestMessage) {
			
		}
	}

	@Override
	public void onMessageFromUI(Message message) {
		if(message instanceof ViewportHelloMessage) { // Received from ViewportContainer UIObject when it has inited. We can then manage it
			viewport_container = ((ViewportHelloMessage)message).viewport_container;
		}

		/*else if(message instanceof NetListRefreshRequestMessage) {
			sendToBackend(message);
		}*/
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
