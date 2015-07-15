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
	}

	@Override
	protected void onUpdateEvents() {
		super.onUpdateEvents();

		for(UIObject x : new ArrayList<UIObject>(children)) // XXX Yuck, copying many times through a frame, is that okay?
			x.updateEvents(draw_context);
	}

	public void add(UIObject obj, boolean top) {
		assert obj.parent != null : "Object can not be a child of anything when adding to a group";
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
		for(int i = children.size() - 1; i > -1; i-- )
			if(children.get(i) == obj)
				children.remove(i);
	}

	public ArrayList<UIObject> getChildren() {
		return new ArrayList<UIObject>(children);
	}

	public ArrayList<UIObject> getAllChildren() {
		ArrayList<UIObject> result = new ArrayList<UIObject>();

		for(UIObject x : children) {
			result.add(x);
			if(x instanceof UIGroup)
				result.addAll(((UIGroup) x).getAllChildren());
		}

		return result;
	}
}
