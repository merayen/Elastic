package net.merayen.elastic.ui.objects.top.viewbar;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;
import net.merayen.elastic.ui.objects.top.megamenu.MegaMenu;

public class ViewBar extends AutoLayout {
	public ViewBar() {
		super(new LayoutMethods.HorizontalBox(2, 100000));
	}

	public float width;
	private final float height = 20;
	private final MegaMenu menu = new MegaMenu();
	protected final UIObject content = new UIObject();

	@Override
	public void onInit() {
		add(menu);
		add(content);
	}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.line(0, height, width, height);
	}
}
