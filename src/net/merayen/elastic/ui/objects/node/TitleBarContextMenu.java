package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem;

class TitleBarContextMenu extends UIObject {
	private final ContextMenu menu;

	TitleBarContextMenu(UIObject titlebar) {
		menu = new ContextMenu(titlebar, new ContextMenu.Handler() {
			@Override
			public void onSelect(ContextMenuItem item) {}
		});
	}

	@Override
	protected void onEvent(IEvent event) {
		menu.handle(event);
	}
}
