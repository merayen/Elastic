package net.merayen.merasynth.ui.objects.top.menu;

import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.util.MouseHandler;

public class MenuList extends UIGroup {
	public static abstract class Handler {
		public void onOutsideClick() {}
	}

	private ArrayList<MenuListItem> items = new ArrayList<MenuListItem>();
	private MouseHandler mouse_handler;
	private Handler handler;

	protected void onInit() {
		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseOutsideDown(Point position) {
				if(handler != null)
					handler.onOutsideClick();
			}
		});
		
		super.onInit();
	}

	protected void onDraw() {
		int i = 0;
		for(MenuListItem x : items) {
			x.translation.y = i; 
			i += x.getMenuItemHeight();
			x.width = getMenuWidth() - 0.2f;
		}

		draw.setColor(150, 150, 150);
		draw.fillRect(0, 0, getMenuWidth(), getMenuHeight());

		draw.setColor(80, 80, 80);
		draw.fillRect(0.1f, 0.1f, getMenuWidth() - 0.2f, getMenuHeight() - 0.2f);

		super.onDraw();
	}

	protected void onEvent(IEvent e) {
		mouse_handler.handle(e);
	}

	public void addMenuItem(MenuListItem menu_item) {
		add(menu_item);
		items.add(menu_item);
	}

	public void removeMenuItem(MenuListItem menu_item) {
		remove(menu_item);
		items.remove(menu_item); // XXX Virker faktisk dette?
	}

	public float getMenuWidth() {
		float w = 2f;
		for(MenuListItem x : items)
			w = Math.max(w, x.getMenuItemWidth());
		return w;
	}

	public float getMenuHeight() {
		float h = 0;
		for(MenuListItem x : items)
			h += x.getMenuItemHeight();
		return h;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
