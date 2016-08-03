package net.merayen.elastic.ui.objects.top.menu;

import java.util.ArrayList;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.util.MouseHandler;

public class MenuList extends UIObject {
	public static abstract class Handler {
		public void onOutsideClick() {}
	}

	private ArrayList<MenuListItem> items = new ArrayList<MenuListItem>();
	private MouseHandler mouse_handler;
	private Handler handler;

	@Override
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

	@Override
	protected void onDraw() {
		int i = 0;
		for(MenuListItem x : items) {
			x.translation.y = i; 
			i += x.getMenuItemHeight();
			x.width = getMenuWidth() - 2f;
		}

		draw.setColor(150, 150, 150);
		draw.fillRect(0, 0, getMenuWidth(), getMenuHeight());

		draw.setColor(80, 80, 80);
		draw.fillRect(1f, 1f, getMenuWidth() - 2f, getMenuHeight() - 2f);

		super.onDraw();
	}

	@Override
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
		float w = 20f;
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
