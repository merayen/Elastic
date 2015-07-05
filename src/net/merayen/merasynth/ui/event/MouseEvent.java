package net.merayen.merasynth.ui.event;

import java.util.ArrayList;
import java.util.Comparator;

import net.merayen.merasynth.ui.objects.Top;
import net.merayen.merasynth.ui.objects.UIObject;

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
	
	public ArrayList<UIObject> objects_hit;
	
	public MouseEvent(java.awt.event.MouseEvent mouse_event, action_type action) {
		this.mouse_event = mouse_event;
		this.action = action;
	}
	
	// XXX Move hit testing out in a "hit test"-like class? 
	public void calcHit(Top top) {
		assert objects_hit == null;

		objects_hit = new ArrayList<UIObject>();
		
		int x = mouse_event.getX();
		int y = mouse_event.getY();

		ArrayList<UIObject> objs = top.getAllChildren();
		objs.add(top);

		for(UIObject o : objs)
			if(
				o.outline_abs != null &&
				x >= o.outline_abs.x &&
				y >= o.outline_abs.y &&
				x < o.outline_abs.x + o.outline_abs.width &&
				y < o.outline_abs.y + o.outline_abs.height
			)
				objects_hit.add(o);
		
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
