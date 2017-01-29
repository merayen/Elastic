package net.merayen.elastic.ui.objects.top.menu;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class MenuBarItem extends UIObject {
	/*
	 * You know what this is, so no need to write anything in the description here. TODO Remove description
	 */
	public static abstract class Handler {
		public void onOpen() {}
	}

	public String label = "";
	public MenuList menu_list = new MenuList();

	private Handler handler;
	private boolean menu_shown; 
	private float label_width;
	private MouseHandler mouse_handler;
	private boolean over = false;
	private long allow_closing;
	private boolean close_menu;

	protected void onInit() {
		menu_list.translation.y = 20f;
		menu_list.setHandler(new MenuList.Handler() {
			@Override
			public void onOutsideClick() { 
				if(allow_closing < System.currentTimeMillis())
				close_menu = true;
			}
		});
		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseOver() {
				over = true;
			}

			@Override
			public void onMouseOut() {
				over = false;
			}

			@Override
			public void onMouseDown(Point position) {
				toggleMenu();
			}
		});
	}

	@Override
	protected void onDraw() {
		draw.setFont("Geneva", 12f);
		label_width = draw.getTextWidth(label);

		draw.setColor(80, 80, 80);
		draw.fillRect(0f, 0.0f, label_width + 10f, 18f);

		draw.setColor(80, 80, 80);
		draw.text(label, 5f, 15f);

		if(over)
			draw.setColor(255, 255, 200);
		else
			draw.setColor(200, 200, 200);

		draw.text(label, 4.5f, 14.5f);
	}

	@Override
	protected void onUpdate() {
		if(close_menu) {
			hideMenu();
			close_menu = false;
		}
	}

	protected void onEvent(IEvent e) {
		mouse_handler.handle(e);
	}

	public float getLabelWidth() {
		return label_width;
	}

	private void toggleMenu() {
		if(!menu_shown) {
			if(handler != null) handler.onOpen();
			showMenu();
		} else {
			hideMenu();
		}
	}

	public void showMenu() {
		if(menu_list.getParent() == null)
			add(menu_list);

		allow_closing = System.currentTimeMillis() + 200;
	}

	public void hideMenu() {
		if(menu_list.getParent() != null)
			remove(menu_list);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
