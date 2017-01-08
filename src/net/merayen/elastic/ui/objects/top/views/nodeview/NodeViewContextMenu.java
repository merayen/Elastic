package net.merayen.elastic.ui.objects.top.views.nodeview;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem;
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem;
import net.merayen.elastic.ui.objects.top.views.nodeview.addnode.AddNodePopup;

class NodeViewContextMenu extends UIObject {
	private final ContextMenu menu;

	private final TextContextMenuItem add_node_item = new TextContextMenuItem("Add node");

	NodeViewContextMenu(UIObject background) {
		UIObject self = this;
		menu = new ContextMenu(background, 8, new ContextMenu.Handler() {
			@Override
			public void onSelect(ContextMenuItem item) { // TODO move stuff below out to a separate class
				System.out.println(((TextContextMenuItem)item).text);

				if(item == add_node_item) {
					new AddNodePopup(self);
				}
			}
		});

		menu.addMenuItem(add_node_item);
		menu.addMenuItem(new TextContextMenuItem("Something else"));
		menu.addMenuItem(new TextContextMenuItem("A very long text that contains many letters, no really, it has lots of letters"));
		menu.addMenuItem(new TextContextMenuItem("T"));
	}

	@Override
	protected void onEvent(IEvent event) {
		menu.handle(event);
	}
}
