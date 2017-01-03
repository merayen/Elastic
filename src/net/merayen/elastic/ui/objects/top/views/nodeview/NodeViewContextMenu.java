package net.merayen.elastic.ui.objects.top.views.nodeview;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem;
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem;

class NodeViewContextMenu extends UIObject {
	private final ContextMenu menu;

	NodeViewContextMenu(UIObject background) {
		menu = new ContextMenu(background, 8, new ContextMenu.Handler() {
			@Override
			public void onSelect(ContextMenuItem item) {
				System.out.println(((TextContextMenuItem)item).text);
			}
		});
		menu.addMenuItem(new TextContextMenuItem("Add node"));
		menu.addMenuItem(new TextContextMenuItem("Something else"));
		menu.addMenuItem(new TextContextMenuItem("A very long text that contains many letters, no really, it has lots of letters"));
		menu.addMenuItem(new TextContextMenuItem("T"));
	}

	@Override
	protected void onEvent(IEvent event) {
		menu.handle(event);
	}
}
