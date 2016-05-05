package net.merayen.elastic.graphlist;

import java.util.ArrayList;
import java.util.List;

public class GIterator {
	private final GList glist;

	public GIterator(GList glist) {
		this.glist = glist;
	}

	/**
	 * Get all objects that are 
	 * @return
	 */
	public List<GObject> getTopObjects() {
		List<GObject> result = new ArrayList<>();
		for(GObject o : glist.list)
			if(o.parent == null)
				result.add(o);

		return result;
	}

	public GObject getTop(GObject o) {
		while(o.parent != null)
			o = o.parent;

		return o;
	}

	public List<GObject> getChildren(GObject o) { // Not ordered
		List<GObject> result = new ArrayList<>();
		List<GObject> to_visit = new ArrayList<>();
		to_visit.add(o);

		while(to_visit.size() > 0) {
			GObject current = to_visit.remove(0);

			for(GObject g : current.children) {
				result.add(g);
				to_visit.add(g);
			}
		}

		return result;
	}

	/**
	 * Retrieve all GObjects that are *not* on the same network.
	 */
	public List<GObject> getNonConnected(GObject o) {
		List<GObject> result = new ArrayList<>(glist.list);
		o = getTop(o);
		result.removeAll(getChildren(o));
		result.remove(o);
		return result;
	}

	public interface Iterate {
		public void stepDown();
		public void evaluate(GObject o);
		public void stepUp();
	}

	/**
	 * Iterates from top to bottom from a GObject. 
	 */
	public void iterateFrom(GObject o, Iterate func) {
		if(glist.locked)
			throw new RuntimeException("Already locked");

		glist.locked = true;
		try {
			visit(o, func);
		} finally {
			glist.locked = false;
		}
	}

	private void visit(GObject o, Iterate func) {
		func.evaluate(o);

		if(o.children.size() > 0) {
			func.stepDown();
	
			for(GObject x : o.children)
				visit(x, func);
	
			func.stepUp();
		}
	}
}
