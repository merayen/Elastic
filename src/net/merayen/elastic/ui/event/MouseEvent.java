package net.merayen.elastic.ui.event;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.Point;

/**
 * TODO handle multiple surfaces (window) better. It does not work as of now.
 */
public class MouseEvent implements IEvent {
	public enum Action {
		DOWN,
		UP,
		OVER,
		OUT,
		MOVE
	}

	public enum Button {
		LEFT,
		MIDDLE,
		RIGHT
	}

	private final String surface_id;
	public final Action action;
	public final Button button;

	public final int x, y;

	public List<UIObject> objects_hit;

	public MouseEvent(String surface_id, int x, int y, Action action, Button button) {
		this.surface_id = surface_id;
		this.x = x;
		this.y = y;
		this.action = action;
		this.button = button;
	}

	// XXX Move hit testing out in a "hit test"-like class? 
	private List<UIObject> calcHit(Window uiobject) {
		List<UIObject> hits = new ArrayList<UIObject>();

		List<UIObject> objs = uiobject.search.getAllChildren();
		objs.add(uiobject);

		for(UIObject o : objs)
			if(
				o.isInitialized() &&
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

			if(uiobject != null)
				uiobject.debug.set("MouseEvent.calcHit", m);
		} else {
			if(uiobject != null)
				uiobject.debug.unset("MouseEvent.calcHit");
		}

		return hits;
	}

	public boolean isHit(UIObject uiobject) {
		if(objects_hit == null)
			objects_hit = calcHit(UINodeUtil.getWindow(uiobject));

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
			objects_hit = calcHit(UINodeUtil.getWindow(uiobject));

		return objects_hit.indexOf(uiobject);
	}

	/**
	 * Get mouse cursor position on UIObject.
	 */
	public Point getOffset(UIObject uiobject) {
		return uiobject.getRelativeFromAbsolute(x, y);
	}

	@Override
	public String getSurfaceID() {
		return surface_id;
	}
}
