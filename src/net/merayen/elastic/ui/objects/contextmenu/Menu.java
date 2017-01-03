package net.merayen.elastic.ui.objects.contextmenu;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;

class Menu extends UIObject {
	float radius = 150f;

	private float pointer_x;
	private float pointer_y;

	private int selected = -1;

	private List<ContextMenuItem> items = new ArrayList<>();
	private final int count;

	Menu(int count) {
		this.count = count;
	}

	@Override
	protected void onDraw() {
		int steps = count;

		draw.setColor(200, 200, 200);
		draw.setStroke(10);
		draw.oval(0, 0, radius * 2, radius * 2);

		float item_radius = radius / (3 * (steps / (float)8)) ;
		boolean marked = false;

		selected = -1;

		for(int i = 0; i < steps; i++) {
			boolean active = false;
			float x = radius - item_radius + (float)Math.sin(i / (float)steps * Math.PI * 2) * radius;
			float y = radius - item_radius + (float)Math.cos(i / (float)steps * Math.PI * 2) * radius;

			if(!marked && (Math.abs(pointer_x) > radius / 3 || Math.abs(pointer_y) > radius / 3)) {
				double pointer = (((Math.atan2(-pointer_x, -pointer_y) / Math.PI + 1) / 2) * steps + 0.5f) % steps;
				if((int)pointer == i) {
					marked = true;
					active = true;
				}
			}

			int menu_index = Math.floorMod(-i + count / 2, steps); // Makes items begin at 12 o'clock
			if(menu_index < items.size()) {
				ContextMenuItem cmi = items.get(menu_index);
				cmi.active = active;
				cmi.item_radius = item_radius;
				cmi.translation.x = x;
				cmi.translation.y = y;
				if(active)
					selected = menu_index;
			}
		}
	}

	void setPointer(float x, float y) {
		pointer_x = x;
		pointer_y = y;
	}

	void addMenuItem(ContextMenuItem item) {
		items.add(item);
		add(item);
	}

	ContextMenuItem getSelected() {
		if(selected > -1 && selected < items.size())
			return items.get(selected);

		return null;
	}
}
