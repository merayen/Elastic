package net.merayen.merasynth.ui.event;

import java.util.ArrayList;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.objects.top.Top;

public class MouseEvent implements IEvent {
	/*
	 * Contains a mouse event
	 */

	public enum action_type {
		DOWN,
		UP,
		OVER,
		OUT,
		MOVE
	}

	public java.awt.event.MouseEvent mouse_event;
	public action_type action;
	private final java.awt.Dimension resolution;

	public ArrayList<UIObject> objects_hit;

	public MouseEvent(java.awt.event.MouseEvent mouse_event, action_type action, java.awt.Dimension resolution) {
		this.mouse_event = mouse_event;
		this.action = action;
		this.resolution = resolution;
	}

	// XXX Move hit testing out in a "hit test"-like class? 
	public void calcHit(Top top) {
		assert objects_hit == null;

		objects_hit = new ArrayList<UIObject>();

		int x_px = mouse_event.getX();
		int y_px = mouse_event.getY();
		float x = x_px;
		float y = y_px;

		ArrayList<UIObject> objs = top.getAllChildren();
		objs.add(top);

		System.out.printf("Mouse: %f\t%f\t\n", x, y);

		for(UIObject o : objs) {
			if(o instanceof net.merayen.merasynth.client.ui_test.UI)
				System.out.println(o.outline_abs_px);

			if(
				o.isReady() &&
				o.absolute_translation.visible &&
				o.outline_abs_px != null &&
				x >= o.outline_abs_px.x1 &&
				y >= o.outline_abs_px.y1 &&
				x < o.outline_abs_px.x2 &&
				y < o.outline_abs_px.y2
			) {
				if(o instanceof net.merayen.merasynth.client.ui_test.UI)
					System.out.println("Hit");
				objects_hit.add(o);
			}
		}

		objects_hit.sort( (a,b) -> b.draw_z - a.draw_z );
	}

	public UIObject getTopmostHit() {
		assert objects_hit != null;

		if(objects_hit.size() > 0)
			return objects_hit.get(0);
		else
			return null;
	}
}
