package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.event.MouseEvent.Button;
import net.merayen.elastic.ui.objects.top.menu.MenuList;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class DropDown extends UIObject {
	public float width = 100;
	private final float height = 20;
	private MouseHandler mouse_handler;
	private boolean open;
	public MenuList menu_list = new MenuList();

	@Override
	protected void onInit() {
		menu_list.setHandler(new MenuList.Handler() {
			@Override
			public void onOutsideClick() {
				if(menu_list.getParent() != null)
					toggle();
			}
		});
		mouse_handler = new MouseHandler(this, Button.LEFT);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDown(Point position) {
				toggle();
			}

			@Override
			public void onMouseOutsideDown(Point global_position) {
				//if(open)
				//	toggle();
			}
		});
	}

	@Override
	protected void onDraw() {
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.rect(0, 0, width, height);

		draw.rect(width - 20, 0, 20, height);
	}

	@Override
	protected void onUpdate() {
		menu_list.translation.y = height;
	}

	@Override
	protected void onEvent(UIEvent e) {
		mouse_handler.handle(e);
	}

	private void toggle() {
		if(menu_list.getParent() == null)
			add(menu_list);
		else
			remove(menu_list);
	}
}
