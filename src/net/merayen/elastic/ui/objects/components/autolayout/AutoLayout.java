package net.merayen.elastic.ui.objects.components.autolayout;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.util.Point;

public class AutoLayout extends UIObject {
	public interface Placement {
		public Point place(int index, UIObject previous, UIObject current);
	}

	private final Placement placement;

	private float margin = 2;
	private boolean schedule_placement;

	public AutoLayout(Placement placement) {
		this.placement = placement;
	}

	@Override
	public void add(UIObject element, int index) {
		super.add(element, index);
		schedule_placement = true;
	}

	@Override
	protected void onUpdate() {
		if(schedule_placement) {
			schedule_placement = false;
			place();
		}
	}

	private void place() {
		float x = 0;
		float y = 0;
		int i = 0;
		UIObject previous = null;

		for(UIObject button : search.getChildren()) {
			Point point = placement.place(i, previous, button);
			x += point.x;
			y += point.y;
			button.translation.x = x;
			button.translation.y = y;
			i++;
			previous = button;
		}
	}
}
