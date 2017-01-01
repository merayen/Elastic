package net.merayen.elastic.ui.objects.contextmenu;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;

class Menu extends UIObject {
	float radius = 150f;

	private float pointer_x;
	private float pointer_y;

	private int selected = -1;

	private final static int STEPS = 8;

	private List<ContextMenuItem> items = new ArrayList<>();

	@Override
	protected void onDraw() {
		draw.setColor(200, 200, 200);
		draw.setStroke(10);
		draw.oval(0, 0, radius * 2, radius * 2);

		float item_radius = radius / (3 * (STEPS / (float)8)) ;
		boolean marked = false;

		for(int i = 0; i < STEPS; i++) {
			boolean active = false;
			float x = radius - item_radius + (float)Math.sin(i / (float)STEPS * Math.PI * 2) * radius;
			float y = radius - item_radius + (float)Math.cos(i / (float)STEPS * Math.PI * 2) * radius;

			if(!marked && (Math.abs(pointer_x) > radius / 4 || Math.abs(pointer_y) > radius / 4)) {
				double pointer = (((Math.atan2(-pointer_x, -pointer_y) / Math.PI + 1) / 2) * STEPS + 0.5f) % STEPS;
				if((int)pointer == i) {
					marked = true;
					active = true;
					selected = i;
				}
			}

			if(active)
				draw.setColor(100, 150, 100);
			else
				draw.setColor(100, 100, 100);
			draw.fillOval(x, y, item_radius * 2, item_radius * 2);

			if(active)
				draw.setColor(200, 255, 200);
			else
				draw.setColor(50, 50, 50);
			draw.setStroke(item_radius / 10);
			draw.oval(x,y, item_radius * 2, item_radius * 2);
		}
	}

	void setPointer(float x, float y) {
		pointer_x = x;
		pointer_y = y;
	}
}
