package net.merayen.elastic.ui.objects.top.views.nodeview;

import net.merayen.elastic.system.intercom.CreateNodeMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem;
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem;
import net.merayen.elastic.ui.objects.top.views.nodeview.addnode.AddNodePopup;
import net.merayen.elastic.uinodes.BaseInfo;
import net.merayen.elastic.util.NodeUtil;
import net.merayen.elastic.util.Point;
import org.jetbrains.annotations.NotNull;

class NodeViewContextMenu extends UIObject {
	private final ContextMenu menu;
	private final String node_id;

	private final TextContextMenuItem add_node_item = new TextContextMenuItem("Add node");

	NodeViewContextMenu(UIObject background, String node_id) {
		if(node_id == null)
			throw new RuntimeException("nodeId can not be null, we must be based on being inside a node");

		this.node_id = node_id;
		UIObject self = this;
		menu = new ContextMenu(background, 8, MouseEvent.Button.RIGHT);

		menu.setHandler(new ContextMenu.Handler() {
			@Override
			public void onMouseDown(@NotNull Point position) {}

			@Override
			public void onSelect(ContextMenuItem item, Point position) { // TODO move stuff below out to a separate class
				if(item == add_node_item) {
					new AddNodePopup(self, info -> createNode(info, position));
				}
			}
		});

		menu.addMenuItem(add_node_item);
		menu.addMenuItem(new TextContextMenuItem("Something else"));
		menu.addMenuItem(new TextContextMenuItem("A very long text that contains many letters, no really, it has lots of letters"));
		menu.addMenuItem(new TextContextMenuItem("T"));
	}

	private void createNode(BaseInfo info, Point position) {
		String[] path = info.getClass().getName().split("\\.");
		String name = path[path.length - 2];

		String node_id = NodeUtil.createID();
		sendMessage(new CreateNodeMessage(node_id, NodeUtil.getNodeName(name), NodeUtil.getNodeVersion(name), this.node_id)); // TODO group shall not be null, but 
		sendMessage(new NodeParameterMessage(node_id, "ui.java.translation.x", position.x));
		sendMessage(new NodeParameterMessage(node_id, "ui.java.translation.y", position.y));
		// TODO also send parameter for X and Y translation?
	}

	@Override
	public void onEvent(UIEvent event) {
		menu.handle(event);
	}
}
