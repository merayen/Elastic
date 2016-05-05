package net.merayen.elastic.graphlist;

import java.util.ArrayList;
import java.util.List;

public class GList {
	List<GObject> list = new ArrayList<>();
	boolean locked;

	public GObject create() {
		checkLock();
		GObject o = new GObject();
		list.add(o);
		return o;
	}

	public void add(GObject parent, GObject child) {
		checkLock();
		check(parent);
		check(child);

		if(parent == child)
			throw new RuntimeException("Parent can not be the same as child");

		if(child.parent != null)
			throw new RuntimeException("Child already has a parent");

		if(parent.children.contains(child))
			throw new RuntimeException("Should not happen");

		parent.children.add(child);
		child.parent = parent;
	}

	/**
	 * Remove GObject child from its parent.
	 * Will still be in the tree and can be re-used and added later on.
	 */
	public void remove(GObject parent, GObject child) {
		checkLock();
		check(parent);
		check(child);

		if(parent == child)
			throw new RuntimeException("Parent can not be the same as child");

		parent.children.remove(child);
		child.parent = null;
	}

	/**
	 * Delete object completely from this graph.
	 * Any children looses this parent.
	 * GObject can not be reused.
	 */
	public void delete(GObject o) {
		checkLock();
		check(o);

		if(o.parent != null) // Detach parent
			remove(o.parent, o); 

		for(GObject g : o.children) // Detach children
			remove(o, g);

		list.remove(o);
	}

	private void checkLock() {
		if(locked)
			throw new RuntimeException("GList is locked. Could be you are iterating over it");
	}

	private void check(GObject o) {
		if(!list.contains(o))
			throw new RuntimeException("GObject does not exist in this GList");
	}
}
