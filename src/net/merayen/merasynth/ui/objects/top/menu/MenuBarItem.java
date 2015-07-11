package net.merayen.merasynth.ui.objects.top.menu;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.util.MouseHandler;

public class MenuBarItem extends Group {
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
	private long menu_hide_delay;

	protected void onInit() {
		menu_list.translation.y = 2f;
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
		draw.setFont("Geneva", 1.2f);
		label_width = draw.getTextWidth(label);

		draw.setColor(80, 80, 80);
		draw.fillRect(0f, 0.0f, label_width + 1f, 1.8f);

		draw.setColor(80, 80, 80);
		draw.text(label, 0.5f, 1.5f);

		if(over)
			draw.setColor(255, 255, 200);
		else
			draw.setColor(200, 200, 200);
		draw.text(label, 0.45f, 1.45f);

		super.onDraw();
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
		if(!menu_shown)
			add(menu_list);
		allow_closing = System.currentTimeMillis() + 500;
		menu_shown = true;
	}

	public void hideMenu() {
		this.sendEvent(new DelayEvent(new Runnable() {

			@Override
			public void run() {
				innerHideMenu();
			}
			
		}));
	}

	private void innerHideMenu() {
		if(menu_shown)
			remove(menu_list);
		menu_shown = false;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
