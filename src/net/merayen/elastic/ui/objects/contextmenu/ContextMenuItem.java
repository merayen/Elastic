package net.merayen.elastic.ui.objects.contextmenu;

import net.merayen.elastic.ui.UIObject;

public abstract class ContextMenuItem extends UIObject {
	boolean active; // true if being selected
	float item_radius;
	float x, y;

	@Override
	protected void onDraw() {
		if(active)
			draw.setColor(100, 150, 100);
		else
			draw.setColor(100, 100, 100);

		draw.fillOval(0, 0, item_radius * 2, item_radius * 2);

		if(active)
			draw.setColor(200, 255, 200);
		else
			draw.setColor(50, 50, 50);

		draw.setStroke(item_radius / 10);
		draw.oval(0,0, item_radius * 2, item_radius * 2);
	}

	protected float getRadius() {
		return item_radius;
	}
}
