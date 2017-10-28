package net.merayen.elastic.ui.objects.top.viewbar;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.megamenu.MegaMenu;

public class ViewBar extends UIObject {
	public float width;
	private final float height = 20;
	private final MegaMenu menu = new MegaMenu();
	protected final UIObject content = new UIObject();

	@Override
	protected void onInit() {
		menu.translation.x = 2;
		menu.translation.y = 2;
		add(content);
		add(menu);
	}

	@Override
	protected void onDraw() {
		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.line(0, height, width, height);
	}
}
