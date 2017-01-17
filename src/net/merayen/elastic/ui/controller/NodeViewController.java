package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.Config;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.top.Window;
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
		if(Config.ui.debug.messages)
			System.out.printf("UI NodeViewController %s is processing message: %s%s\n", this.toString().split("@")[1], message.getClass().getSimpleName(), message.toString().split("@")[1]);

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;

			for(NodeView nv : getNodeViews())
				nv.addNode(m.node_id, m.name, m.version);

		} else if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			for(NodeView nv : getNodeViews())
				nv.getNode(m.node_id).executeMessage(message);

		} else if(message instanceof NodeDataMessage) {
			NodeDataMessage m = (NodeDataMessage)message;

			for(NodeView nv : getNodeViews()) {
				UINode n = nv.getNode(m.node_id);
				if(n != null) // Why check?
					n.executeMessage(message);
			}

		} else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;

			for(NodeView nv : getNodeViews())
				nv.getNode(m.node_id).executeMessage(message); // Exception? UI out of sync

		} else if(message instanceof RemoveNodePortMessage) {
			RemoveNodePortMessage m = (RemoveNodePortMessage)message;
			for(NodeView nv : getNodeViews())
				nv.getNode(m.node_id).executeMessage(message); // Exception? UI out of sync

		} else if(message instanceof RemoveNodeMessage) {
			RemoveNodeMessage m = (RemoveNodeMessage)message;
			for(NodeView nv : getNodeViews())
				nv.removeNode(m.node_id); // Exception? UI out of sync

		} else if(message instanceof ResetNetListMessage) { // TODO implement support to only reset a certain group?
			for(NodeView nv : getNodeViews())
				nv.reset();
		}
	}

	@Override
	protected void onMessageFromUI(Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			if(m.key.startsWith("ui.java.")) // TODO rename ui.java to something more local for this NodeViewController
				for(NodeView nv : getNodeViews())
					nv.messageNode(m.node_id, message); // Forward messages with parameters used by us (only)

			sendToBackend(message); // Forward message to backend 
		} else if(message instanceof RemoveNodeMessage) {
			sendToBackend(message);

		} else if(message instanceof CreateNodeMessage) {
			sendToBackend(message);
		}
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
	protected JSONObject onDump() {
		// TODO Serialize... uhm... what?
		return null;
	}

	@Override
	protected void onRestore(JSONObject obj) {}
}
