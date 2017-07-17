package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.Config;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Handles messages sent and received by nodes.
 */
public class NodeViewController extends Controller {
	/**
	 * NodeViews send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	public static class Hello extends Postmaster.Message {}

	/**
	 * NetList accumulated based on all the incoming messages.
	 * We can then resend messages when requested.
	 */
	public final NetList netlist;
	private String top_node_id; // The topmost node. Automatically figured out upon restoration.

	public NodeViewController(Gate gate) {
		super(gate);
		this.netlist = new NetList(); // XXX not in use
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onMessageFromBackend(Postmaster.Message message) {
		if(Config.ui.debug.messages)
			System.out.printf("UI NodeViewController %s is processing message: %s%s\n", this.toString().split("@")[1], message.getClass().getSimpleName(), message.toString().split("@")[1]);

		NetListMessages.apply(netlist, message);

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;

			if(m.parent == null)
				top_node_id = m.node_id; // Found the topmost node

			for(NodeView nv : getNodeViews())
				if(nv.getViewNodeID().equals(m.parent))
					nv.addNode(m.node_id, m.name, m.version, m.parent);

		} else if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			for(NodeView nv : getNodeViews()) {
				UINode n = nv.getNode(m.node_id);
				if(n != null)
					n.executeMessage(message);
			}

		} else if(message instanceof NodeDataMessage) {
			NodeDataMessage m = (NodeDataMessage)message;
			for(NodeView nv : getNodeViews()) {
				UINode n = nv.getNode(m.node_id);
				if(n != null)
					n.executeMessage(message);
			}

		} else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;
			for(NodeView nv : getNodeViews()) {
				UINode n = nv.getNode(m.node_id);
				if(n != null)
					n.executeMessage(message);
			}

		} else if(message instanceof RemoveNodePortMessage) {
			RemoveNodePortMessage m = (RemoveNodePortMessage)message;
			for(NodeView nv : getNodeViews()) {
				UINode n = nv.getNode(m.node_id);
				if(n != null)
					n.executeMessage(message);
			}

		} else if(message instanceof RemoveNodeMessage) {
			RemoveNodeMessage m = (RemoveNodeMessage)message;
			for(NodeView nv : getNodeViews())
				if(nv.getNode(m.node_id) != null)
					nv.removeNode(m.node_id); // Exception? UI out of sync

		} else if(message instanceof BeginResetNetListMessage) { // TODO implement support to only reset a certain group?
			for(NodeView nv : getNodeViews())
				nv.reset();
		}

		// UINet
		if(message instanceof NodeConnectMessage || message instanceof NodeDisconnectMessage || message instanceof RemoveNodeMessage || message instanceof RemoveNodePortMessage)
			for(NodeView nv : getNodeViews()) // Forward message regarding the net, from backend to the UINet, to all NodeViews
				nv.getUINet().handleMessage(message);
	}

	@Override
	protected void onMessageFromUI(Message message) {
		if(message instanceof Hello) {
			registerUs(); // A NodeView says hello. We register ourself on all the NodeViews
			
		} else if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			if(m.key.startsWith("ui.java.")) // TODO rename ui.java to something more local for this NodeViewController
				for(NodeView nv : getNodeViews())
					nv.messageNode(m.node_id, message); // Forward messages with parameters used by us (only)

			sendToBackend(message); // Forward message to backend 
		} else if(
			message instanceof RemoveNodeMessage ||
			message instanceof CreateNodeMessage ||
			message instanceof CreateCheckpointMessage ||
			message instanceof NodeDataMessage ||
			message instanceof NodeConnectMessage ||
			message instanceof NodeDisconnectMessage
		) {
			sendToBackend(message);
		} else if (message instanceof NetListRefreshRequestMessage) { // Move it out to a separate controller, with only purpose to accumulate the netlist and resend it?
			List<Postmaster.Message> messages = new ArrayList<>();
			messages.add(new BeginResetNetListMessage());
			messages.addAll(NetListMessages.disassemble(netlist));
			messages.add(new FinishResetNetListMessage());
			for(Postmaster.Message m : messages)
				onMessageFromBackend(m);
		}
	}

	private List<NodeView> getNodeViews() {
		List<NodeView> result = new ArrayList<>();

		for(Window w : getTop().getWindows()) {
			if(w.isInitialized()) {
				for(Viewport vp : w.getViewportContainer().getViewports()) {
					if(vp.view instanceof NodeView) {
						NodeView nv = (NodeView)vp.view;
						if(nv.getViewNodeID() != null)
							result.add(nv);
					}
				}
			}
		}

		return result;
	}

	private void registerUs() {
		for(Window w : getTop().getWindows())
			if(w.isInitialized())
				for(Viewport vp : w.getViewportContainer().getViewports())
					if(vp.view instanceof NodeView)
						((NodeView)vp.view).node_view_controller = this; // Set us on the NodeViews, so that they can call us
	}

	@Override
	protected JSONObject onDump() {
		// TODO Serialize... uhm... what?
		return null;
	}

	@Override
	protected void onRestore(JSONObject obj) {}

	public String getTopNodeID() {
		return top_node_id;
	}
}
