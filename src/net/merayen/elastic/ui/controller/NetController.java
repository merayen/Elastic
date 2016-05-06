package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.system.intercom.NodeConnectMessage;
import net.merayen.elastic.system.intercom.NodeDisconnectMessage;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.util.Postmaster.Message;

public class NetController extends Controller {

	public NetController(Gate gate) {
		super(gate);
	}

	@Override
	public void onMessageFromBackend(Message message) {
		if(message instanceof NodeConnectMessage) {
			
		}
	}

	@Override
	public void onMessageFromUI(Message message) {
		if(message instanceof NodeConnectMessage || message instanceof NodeDisconnectMessage) {
			//sendToBackend(message);

			// Send it back to UI, for all views for now, for testing
			for(NodeView nv : getNodeViews())
				nv.getUINet().handleMessage(message);
		}
	}

	private List<NodeView> getNodeViews() {
		List<NodeView> result = new ArrayList<>();

		if(getTopObject().isInitialized()) {
			for(Viewport vp : getTopObject().getViewportContainer().getViewports()) {
				if(vp.view instanceof NodeView)
					result.add((NodeView)vp.view);
			}
		}

		return result;
	}
}
