package net.merayen.elastic.ui.controller;

import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.intercom.ViewportContainerUpdateMessage;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.util.Postmaster.Message;

public class ViewportController extends Controller {
	List<Viewport> viewports;

	public ViewportController(Gate gate) {
		super(gate);
	}

	@Override
	public void onMessageFromBackend(Message message) {
		// Views are internal for the UI, no interaction with backend here
	}

	@Override
	public void onMessageFromUI(Message message) {
		if(message instanceof ViewportContainerUpdateMessage)
			viewports = ((ViewportContainerUpdateMessage)message).viewports;
	}

	@Override
	public JSONObject dump() {
		// TODO Auto-generated method stub
		return null;
	}

}
