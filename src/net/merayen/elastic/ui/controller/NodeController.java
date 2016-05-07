package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.system.intercom.NodeCreatedMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Handles messages sent and received by nodes.
 */
public class NodeController extends Controller {
	public NodeController(Gate gate) {
		super(gate);
	}

	@Override
	public void onMessageFromBackend(Postmaster.Message message) {
		if(message instanceof NodeCreatedMessage) {
			NodeCreatedMessage m = (NodeCreatedMessage)message;
			System.out.println("Got message: " + m);

			for(NodeView nv : getNodeViews())
				nv.addNode(m.node_id, m.name, m.version);

		}
	}

	@Override
	public void onMessageFromUI(Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			// Distribute message to all other NodeViews, so they get updated
			for(NodeView nv : getNodeViews())
				nv.messageNode(m.node_id, m);

			sendToBackend(message); // Forward message to backend 
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
