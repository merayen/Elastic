package net.merayen.elastic.ui.event;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.Top;

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

	public List<UIObject> objects_hit;

	public MouseEvent(java.awt.event.MouseEvent mouse_event, action_type action) {
		this.mouse_event = mouse_event;
		this.action = action;
	}

	// XXX Move hit testing out in a "hit test"-like class? 
	private List<UIObject> calcHit(UIObject uiobject) {
		List<UIObject> hits = new ArrayList<UIObject>();

		int x_px = mouse_event.getX();
		int y_px = mouse_event.getY();
		float x = x_px;
		float y = y_px;

		List<UIObject> objs = uiobject.search.getAllChildren();
		objs.add(uiobject);

		for(UIObject o : objs)
			if(
				o.isInitialized() &&
				o.absolute_translation.visible &&
				o.outline_abs_px != null &&
				x >= o.outline_abs_px.x1 &&
				y >= o.outline_abs_px.y1 &&
				x < o.outline_abs_px.x2 &&
				y < o.outline_abs_px.y2
			)
				hits.add(o);

		hits.sort( (a,b) -> b.draw_z - a.draw_z );

		if(hits.size() > 0) {
			String m = "Object hit: ";
			for(UIObject o : hits)
				m += o.getClass().getSimpleName() + ", ";

			((Top)uiobject.search.getTop()).debug.set("MouseEvent.calcHit", m);
		} else {
			((Top)uiobject.search.getTop()).debug.unset("MouseEvent.calcHit");
		}

		

		return hits;
	}

	public boolean isHit(UIObject uiobject) {
		if(objects_hit == null)
			objects_hit = calcHit(uiobject.search.getTop());

		if(objects_hit.size() > 0)
			return objects_hit.get(0) == uiobject;

		return false;
	}

	/**
	 * Returns where in the z-index this object gets hit.
	 * 0 = direct hit (mouse pointer right over it)
	 * more than 0 = UIObject is indirectly hit (one or more uiobjects over)
	 * -1 = not hit at all
	 */
	public int hitDepth(UIObject uiobject) {
		if(objects_hit == null)
			objects_hit = calcHit(uiobject.search.getTop());

		return objects_hit.indexOf(uiobject);
	}

	/**
	 * Get mouse cursor position on UIObject.
	 */
	public Point getOffset(UIObject uiobject) {
		return uiobject.getRelativeFromAbsolute(mouse_event.getX(), mouse_event.getY());
	}
}
