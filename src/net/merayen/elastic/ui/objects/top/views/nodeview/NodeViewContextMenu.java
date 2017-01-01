package net.merayen.elastic.ui.objects.top.views.nodeview;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;

class NodeViewContextMenu extends UIObject {
	private final ContextMenu menu;

	NodeViewContextMenu(UIObject background) {
		menu = new ContextMenu(background);
	}

	@Override
	protected void onEvent(IEvent event) {
		menu.handle(event);
	}
}
