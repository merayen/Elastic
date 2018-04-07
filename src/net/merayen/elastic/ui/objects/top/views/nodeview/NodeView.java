package net.merayen.elastic.ui.objects.top.views.nodeview;

import java.util.ArrayList;

import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.controller.NodeViewController;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.event.MouseWheelEvent;
import net.merayen.elastic.ui.objects.UINet;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.util.Movable;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.TaskExecutor;

// TODO accept NetList as input and rebuild ourselves automatically from that
// TODO allow forwarding of node messages from and to the backend.
// TODO make (dis)connecting work again, by sending a message when user tries

/**
 * Main view. Shows all the nodes and their connections.
 */
public class NodeView extends View {
	String node_id; // Node that we show the children of
	private String new_node_id;
	private final NodeViewContainer container = new NodeViewContainer();
	private UINet net;
	private Movable movable;
	private ArrayList<UINode> nodes = new ArrayList<UINode>();
	private static final String UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.%s_%d.%s";
	private final NodeViewBar node_view_bar = new NodeViewBar();
	public NodeViewController node_view_controller; // Automatically set by NodeViewController

	private NodeViewContextMenu context_menu;

	public NodeView() {
		this(null);
	}

	public NodeView(String node_id) {
		super();
		this.node_id = node_id;
		add(container);
		add(node_view_bar);

		net = new UINet();
		container.add(net, 0); // Add the net first (also, drawn behind everything), as addNode() might have already been called

		// Make it possible to move NodeViewContainer by dragging the background
		movable = new Movable(container, container, MouseEvent.Button.LEFT);
	}

	@Override
	public void onInit() {
		super.onInit();

		// Sends a message that will be picked up by NodeViewController, which again will register us
		sendMessage(new NodeViewController.Hello());
	}
	@Override
	public void onDraw(Draw draw) {
		super.onDraw(draw);

		draw.setColor(20, 20, 50);
		draw.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

		node_view_bar.width = getWidth();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(node_id == null && node_view_controller != null) { // Sees if NodeViewController has seen us yet. If yes, we initialize from it
			if(new_node_id == null)
				swapView(node_view_controller.getTopNodeID());
			else
				swapView(new_node_id);
		}
	}

	/**
	 * Add a node.
	 * Node must already be existing in the backend.
	 */
	public void addNode(String node_id, String name, Integer version, String parent) {
		String path = String.format(UI_CLASS_PATH, name, version, "UI");

		UINode uinode;
		try {
			uinode = (UINode)Class.forName(path).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		uinode.setNodeId(node_id);

		nodes.add(uinode);
		container.add(uinode);
	}

	public void removeNode(String node_id) {
		UINode uinode = getNode(node_id);
		nodes.remove(uinode);
		container.remove(uinode);
	}

	public ArrayList<UINode> getNodes() {
		return new ArrayList<UINode>(nodes);
	}

	public UINode getNode(String id) {
		for(UINode x : nodes)
			if(x.getNodeId().equals(id))
				return x;

		return null;
	}

	public UINet getUINet() {
		return net;
	}

	public void messageNode(String node_id, Postmaster.Message message) {
		UINode node = getNode(node_id);

		if(node == null) {
			System.out.printf("WARNING: Node with id %s not found in this NodeView. Out of sync?\n", node_id);
			return;
		}

		node.executeMessage(message);
	}

	private void zoom(float new_scale_x, float new_scale_y) {
		float previous_scale_x = container.getTranslation().scale_x;
		float previous_scale_y = container.getTranslation().scale_y;
		float scale_diff_x = new_scale_x - previous_scale_x;
		float scale_diff_y = new_scale_y - previous_scale_y;
		float current_offset_x = (container.getTranslation().x - getWidth() / 2);
		float current_offset_y = (container.getTranslation().y - getHeight() / 2);

		container.getTranslation().scale_x = new_scale_x;
		container.getTranslation().scale_y = new_scale_y;
		container.getTranslation().x = getWidth() / 2 + current_offset_x + current_offset_x * (-scale_diff_x / new_scale_x);
		container.getTranslation().y = getHeight() / 2 + current_offset_y + current_offset_y * (-scale_diff_y / new_scale_y);
	}

	@Override
	public void onEvent(UIEvent event) {
		super.onEvent(event);

		movable.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			if(isFocused()) {
				float s_x = container.getTranslation().scale_x;
				float s_y = container.getTranslation().scale_y;

				if(e.getOffsetY() < 0) {
					s_x /= 1.1f;
					s_y /= 1.1f;
				}
				else if(e.getOffsetY() > 0) {
					s_x *= 1.1f;
					s_y *= 1.1f;
				}

				zoom(
					Math.max(Math.min(s_x, 10f), 0.1f),
					Math.max(Math.min(s_y, 10f), 0.1f)
				);
			}
		}
	}

	@Override
	public View cloneView() {
		NodeView nv = new NodeView(node_id);
		nv.container.getTranslation().x = container.getTranslation().x;
		nv.container.getTranslation().y = container.getTranslation().y;
		nv.container.getTranslation().scale_x = container.getTranslation().scale_x;
		nv.container.getTranslation().scale_y = container.getTranslation().scale_y;
		return nv;
	}

	public void swapView(String new_node_id) {
		node_id = new_node_id;

		// Ask for sending a new NetList. We queue it up in the ViewportContainer domain, as several NodeViews might have been created simultaneously, we then only send 1 message
		addTask(new TaskExecutor.Task(getClass(), 0, () -> sendMessage(new NetListRefreshRequestMessage())));
	}

	public void reset() {
		System.out.printf("NodeView reset(): %s\n", this);
	
		net.reset();

		for(UINode node : nodes)
			container.remove(node);

		// Set up context menu when right-clicking on the background
		if(context_menu != null)
			container.remove(context_menu);

		if(container.getSearch().getChildren().size() != 1) // Only UINet() should be remaining
			throw new RuntimeException("Should not happen");

		nodes.clear();

		context_menu = new NodeViewContextMenu(container, node_id);
		container.add(context_menu);
	}

	/**
	 * Retrieve the ID of the node this view displays.
	 * @return
	 */
	public String getViewNodeID() {
		return node_id;
	}
}
