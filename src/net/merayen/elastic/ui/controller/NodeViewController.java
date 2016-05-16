package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Handles messages sent and received by nodes.
 */
public class NodeViewController extends Controller {
	public NodeViewController(Gate gate) {
		super(gate);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onMessageFromBackend(Postmaster.Message message) {
		System.out.printf("UI got message: %s\n", message.getClass().getSimpleName());

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;

			for(NodeView nv : getNodeViews())
				nv.addNode(m.node_id, m.name, m.version);

		} else if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			for(NodeView nv : getNodeViews())
				nv.getNode(m.node_id).executeMessage(message);

		} else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;

			for(NodeView nv : getNodeViews())
				nv.getNode(m.node_id).executeMessage(message); // Exception? UI out of sync

		} else if(message instanceof RemoveNodePortMessage) {
			RemoveNodePortMessage m = (RemoveNodePortMessage)message;

			for(NodeView nv : getNodeViews())
				nv.getNode(m.node_id).executeMessage(message); // Exception? UI out of sync

		} else if(message instanceof ResetNetListMessage) { // TODO implement support to only reset a certain group?
			for(NodeView nv : getNodeViews())
				nv.reset();
		}
	}

	@Override
	protected void onMessageFromUI(Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			// Distribute message to all other NodeViews, so they get updated
			// NO! We don't feed it back directly to all other views, the backend will send it to us afterwards anyway
			//for(NodeView nv : getNodeViews())
			//	nv.messageNode(m.node_id, m);

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

	@Override
	protected JSONObject onDump() {
		// TODO Serialize... uhm... what?
		return null;
	}

	@Override
	protected void onRestore(JSONObject obj) {
		
	}
}
