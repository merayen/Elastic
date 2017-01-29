package net.merayen.elastic.ui.objects.top.views.nodeview;

import java.util.ArrayList;

import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.event.MouseWheelEvent;
import net.merayen.elastic.ui.objects.NodeGroupInitiator;
import net.merayen.elastic.ui.objects.UINet;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.top.menu.MenuBar;
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
public class NodeView extends View implements NodeGroupInitiator {
	private String node_group;
	private final NodeViewContainer container = new NodeViewContainer();
	private UINet net;
	private Movable movable;
	private ArrayList<UINode> nodes = new ArrayList<UINode>();
	private static final String UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.%s_%d.%s";
	private final MenuBar menu = new MenuBar();

	private final NodeViewContextMenu context_menu;

	public NodeView() {
		super();
		add(container);

		net = new UINet();
		container.add(net, true); // Add the net first (also, drawn behind everything), as addNode() might have already been called

		// Make it possible to move NodeViewContainer by dragging the background
		movable = new Movable(container, container, MouseEvent.Button.LEFT);

		// Set up context menu when right-clicking on the background
		context_menu = new NodeViewContextMenu(container);
	}

	@Override
	protected void onInit() {
		super.onInit();
		// Ask for sending a new NetList. We queue it up in the ViewportContainer domain, as several NodeViews might have been created simultaneously, we then only send 1 message
		addTask(new TaskExecutor.Task(getClass(), 1000, () -> sendMessage(new NetListRefreshRequestMessage())));

		menu.translation.x = 0;
		menu.translation.y = 0;
		menu.width = 200;
		add(menu);

		container.add(context_menu);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		draw.setColor(20, 20, 50);
		draw.fillRect(2, 2, width - 4, height - 4);

		menu.width = width;
	}

	/**
	 * Add a node.
	 * Node must already be existing in the backend.
	 */
	public void addNode(String node_id, String name, Integer version) {
		String path = String.format(UI_CLASS_PATH, name, version, "UI");

		UINode uinode;
		try {
			uinode = (UINode)Class.forName(path).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		uinode.node_id = node_id;

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
			if(x.node_id.equals(id))
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
		float previous_scale_x = container.translation.scale_x;
		float previous_scale_y = container.translation.scale_y;
		float scale_diff_x = new_scale_x - previous_scale_x;
		float scale_diff_y = new_scale_y - previous_scale_y;
		float current_offset_x = (container.translation.x - width  / 2);
		float current_offset_y = (container.translation.y - height / 2);

		container.translation.scale_x = new_scale_x;
		container.translation.scale_y = new_scale_y;
		container.translation.x = width  / 2 + current_offset_x + current_offset_x * (-scale_diff_x / new_scale_x);
		container.translation.y = height / 2 + current_offset_y + current_offset_y * (-scale_diff_y / new_scale_y);
	}

	@Override
	protected void onEvent(IEvent event) {
		super.onEvent(event);

		movable.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			if(isFocused()) {
				float s_x = container.translation.scale_x;
				float s_y = container.translation.scale_y;

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
		NodeView nv = new NodeView();
		// TODO copy scroll position
		return nv;
	}

	public void reset() {
		System.out.printf("NodeView reset(): %s\n", this);
		net.reset();

		for(UINode node : nodes)
			container.remove(node);

		if(container.search.getAllChildren().size() != 2) // Only UINet() and ContextMenu() should be remaining
			throw new RuntimeException("Should not happen");

		nodes.clear();
	}

	@Override
	public String getGroup() {
		return node_group;
	}
}
