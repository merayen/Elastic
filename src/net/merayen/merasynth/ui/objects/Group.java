package net.merayen.merasynth.ui.objects;

import java.util.ArrayList;

public class Group extends UIObject {
	/*
	 * Main inneholder flere komponenter.
	 */
	static int hei = 0;
	private int id = 1337;
	
	private ArrayList<UIObject> children = new ArrayList<UIObject>();
	private java.awt.Point noe = new java.awt.Point(1,2);

	public Group() {
		super();
		children = new ArrayList<UIObject>();
		id = hei++;
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		for(UIObject x : children)
			this.drawObject(x);
	}
	
	public void add(UIObject obj) {
		assert obj.parent != null : "Object can not be a child of anything when adding to a group";
		children.add(obj);
		obj.parent = this;
	}
	
	public ArrayList<UIObject> getChildren() {
		return new ArrayList<UIObject>(children);
	}
}
