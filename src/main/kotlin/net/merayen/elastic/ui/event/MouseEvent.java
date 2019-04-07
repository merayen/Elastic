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
public class MouseEvent extends UIEvent {
	public enum Action {
		DOWN,
		UP,
		OVER,
		OUT,
		MOVE,

		/**
		 * When dragging something and lets it go
		 */
		DROP,

		/**
		 * When mouse has gone outside a surface
		 */
		OUT_OF_RANGE
	}

	public enum Button {
		LEFT,
		MIDDLE,
		RIGHT
	}

	public final Action action;
	public final Button button;

	/**
	 * id of the cursor. The regular mouse will always be 0, while gamepads will be something else.
	 */
	public final int id;

	public final int x, y;

	List<UIObject> objects_hit;

	public MouseEvent(String surface_id, int id, int x, int y, Action action, Button button) {
		super(surface_id);
		this.id = id;
		this.x = x;
		this.y = y;
		this.action = action;
		this.button = button;
	}

	// XXX Move hit testing out in a "hit test"-like class?
	private List<UIObject> calcHit(Window uiobject) {
		List<UIObject> hits = new ArrayList<UIObject>();

		List<UIObject> objs = uiobject.getSearch().getAllChildren();
		objs.add(uiobject);

		for (UIObject o : objs)
			if (
					o.isInitialized() &&
							o.getOutline_abs_px() != null &&
							x >= o.getOutline_abs_px().x1 &&
							y >= o.getOutline_abs_px().y1 &&
							x < o.getOutline_abs_px().x2 &&
							y < o.getOutline_abs_px().y2
			)
				hits.add(o);

		hits.sort((a, b) -> b.getDraw_z() - a.getDraw_z());

		if (hits.size() > 0) {
			StringBuilder m = new StringBuilder();
			//for(UIObject o : hits) {
			UIObject o = hits.get(0);

			while (o.getParent() != null) {
				String[] s = o.getClass().getName().split("\\.");
				if (s.length > 1) {
					m.append(s[s.length - 2]);
					m.append(".");
				}
				m.append(s[s.length - 1]);
				m.append("   ");

				o = o.getParent();
			}

			uiobject.getDebug().set("MouseEvent.calcHit", m.toString());
			//uiobject.getDebug().set("MouseEvent.calcHit", hits.get(0).getClass().getName());
		} else {
			uiobject.getDebug().unset("MouseEvent.calcHit");
		}

		return hits;
	}

	public boolean isHit(UIObject uiobject) {
		if (objects_hit == null)
			objects_hit = calcHit(UINodeUtil.getWindow(uiobject));

		if (objects_hit.size() > 0)
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
		if (objects_hit == null)
			objects_hit = calcHit(UINodeUtil.getWindow(uiobject));

		return objects_hit.indexOf(uiobject);
	}
}
