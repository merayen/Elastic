package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem;
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem;
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem;
import net.merayen.elastic.util.Point;

class TitleBarContextMenu extends UIObject {
	private final ContextMenu menu;

	private final TextContextMenuItem delete_node = new TextContextMenuItem("Delete node");

	TitleBarContextMenu(UIObject titlebar) {
		menu = new ContextMenu(titlebar, new ContextMenu.Handler() {
			@Override
			public void onSelect(ContextMenuItem item, Point position) {
				if(item == delete_node) {
					UINode node = (UINode)search.parentByType(UINode.class);
					node.delete();
				}
			}
		});

		menu.addMenuItem(new EmptyContextMenuItem());
		menu.addMenuItem(new EmptyContextMenuItem());
		menu.addMenuItem(new EmptyContextMenuItem());
		menu.addMenuItem(new EmptyContextMenuItem());
		menu.addMenuItem(delete_node);
	}

	@Override
	protected void onEvent(UIEvent event) {
		menu.handle(event);
	}
}
