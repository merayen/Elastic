package net.merayen.elastic.ui.objects.top.views;

import net.merayen.elastic.ui.objects.top.MenuBar;

/**
 * The menu, usually at the top.
 */
public class MenuView extends View {
	private MenuBar menu;

	@Override
	protected void onInit() {
		menu = new MenuBar();
		add(menu);
	}

	@Override
	protected void onUpdate() {
		menu.width = width;
		menu.translation.y = height - menu.height;
	}

	@Override
	public View cloneView() {
		return new MenuView();
	}
}
