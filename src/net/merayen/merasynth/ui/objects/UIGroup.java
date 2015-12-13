package net.merayen.merasynth.ui.objects;

import java.util.ArrayList;

import net.merayen.merasynth.ui.util.Search;

public class UIGroup extends UIObject {
	/*
	 * Inneholder flere komponenter.
	 */
	private ArrayList<UIObject> children = new ArrayList<UIObject>();
	protected Search search;

	public UIGroup() {
		super();
		children = new ArrayList<UIObject>();
		search = new Search(this);
	}

	@Override
	protected void onUpdateDraw() {
		super.onUpdateDraw();

		for(UIObject x : new ArrayList<UIObject>(children)) // XXX Yuck, copying many times through a frame, is that okay?
			x.updateDraw(draw_context);

		onChildrenDrawn();
	}

	@Override
	protected void onUpdateEvents() {
		super.onUpdateEvents();

		for(UIObject x : new ArrayList<UIObject>(children)) // XXX Yuck, copying many times through a frame, is that okay?
			x.updateEvents(draw_context);
	}

	/*
	 * Called when all the children for this group has been drawn.
	 */
	protected void onChildrenDrawn() {

	}

	public void add(UIObject obj, boolean top) {
		if(obj.parent != null)
			throw new RuntimeException("Object can not already be a child when adding to a group");

		if(top)
			children.add(0, obj);
		else
			children.add(obj);

		obj.parent = this;
	}

	public void add(UIObject obj) {
		add(obj, false);
	}

	public void remove(UIObject obj) {
		if(obj.parent != this)
			throw new RuntimeException("Object is not a child of us");

		for(int i = children.size() - 1; i > -1; i-- )
			if(children.get(i) == obj) {
				children.remove(i);
				obj.parent = null;
			}
	}

	public ArrayList<UIObject> getChildren() {
		return new ArrayList<UIObject>(children);
	}

	public ArrayList<UIObject> getAllChildren() {
		ArrayList<UIObject> result = new ArrayList<UIObject>();

		for(UIObject x : new ArrayList<UIObject>(children)) {
			result.add(x);
			if(x instanceof UIGroup)
				result.addAll(((UIGroup) x).getAllChildren());
		}

		return result;
	}
}
