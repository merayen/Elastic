package net.merayen.merasynth.ui.objects.top.menu;

import java.util.ArrayList;

import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.UIObject;

public class Bar extends UIGroup {
	public ArrayList<MenuBarItem> items = new ArrayList<MenuBarItem>();
	public float width = 100f;
	private final float height = 2f;
	private UIObject logo;
	private float logo_width;

	protected void onDraw() {
		float w = 1f;
		for(MenuBarItem x : items) {
			x.translation.x = w;
			w += x.getLabelWidth() + 2f;
		}

		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
		draw.setColor(130, 130, 130);
		draw.fillRect(0, height - 0.01f, width, 0.2f);

		if(logo != null) {
			logo.translation.x = width - logo_width;
			logo.translation.y = 0.0f;
		}

		super.onDraw();
	}

	public void addMenuBarItem(MenuBarItem x) {
		add(x);
		items.add(x);
		x.setHandler(new MenuBarItem.Handler() {
			@Override
			public void onOpen() {
				for(MenuBarItem c : items)
					if(c != x)
						c.hideMenu();
			}
		});
	}

	public void removeMenuBarItem(MenuBarItem x) {
		remove(x);
		items.remove(x);
	}

	public void setLogo(UIObject logo, float logo_width) {
		if(this.logo != null)
			remove(this.logo);

		this.logo = logo;
		add(logo);
		this.logo_width = logo_width;
	}
}
