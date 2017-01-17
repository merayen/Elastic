package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.util.Postmaster.Message;

public class NetController extends Controller {
	public NetController(Gate gate) {
		super(gate);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onMessageFromBackend(Message message) {
		if(message instanceof NodeConnectMessage || message instanceof NodeDisconnectMessage || message instanceof RemoveNodeMessage) {
			for(NodeView nv : getNodeViews()) // Forward message regarding the net, from backend to the UINet, to all NodeViews
				nv.getUINet().handleMessage(message);
		}
	}

	@Override
	protected void onMessageFromUI(Message message) {
		if(message instanceof NodeConnectMessage || message instanceof NodeDisconnectMessage)
			sendToBackend(message); // Forward message. Backend will respond with the same message
	}

	private List<NodeView> getNodeViews() {
		List<NodeView> result = new ArrayList<>();

		for(Window w : getTop().getWindows())
			if(w.isInitialized()) {
				for(Viewport vp : w.getViewportContainer().getViewports()) {
					if(vp.view instanceof NodeView)
						result.add((NodeView)vp.view);
				}
			}

		return result;
	}

	@Override
	public JSONObject onDump() {
		return null; // Nothing to serialize. Loaded from backend anyway
	}

	@Override
	protected void onRestore(JSONObject obj) {
		
	}
}
