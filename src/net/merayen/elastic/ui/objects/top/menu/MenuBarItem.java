package net.merayen.elastic.ui.objects.top.menu;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.util.MouseHandler;

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

	protected void onInit() {
		add(menu_list);
		menu_list.translation.visible = false; // Not drawn until we get clicked on
		menu_list.translation.y = 20f;
		menu_list.setHandler(new MenuList.Handler() {
			@Override
			public void onOutsideClick() {
				if(System.currentTimeMillis() > allow_closing) 
					hideMenu();
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
		menu_list.translation.visible = true;
		allow_closing = System.currentTimeMillis() + 200;
	}

	public void hideMenu() {
		menu_list.translation.visible = false;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}