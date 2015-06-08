package net.merayen.merasynth.ui.util;

import java.util.ArrayList;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.UIObject;

public class Search {
	/*
	 * Utility class that lets an uiobject search and iterate through the other nodes
	 */
	
	UIObject obj;
	int depth;
	
	public Search(UIObject obj) {
		this(obj, 1000);
	}
	
	public Search(UIObject obj, int depth) {
		this.obj = obj;
		this.depth = depth;
	}
	
	/*public static Search getTopSearch(UIObject obj) {
		return 
	}*/
	
	public UIObject getTopmost() {
		UIObject top = obj;
		while(top.parent != null)
			top = top.parent;
		
		return top;
	}
	
	public ArrayList<UIObject> searchByType(Class<? extends UIObject> cls) {
		/*
		 * Search downwards for a type
		 */
		ArrayList<UIObject> result = new ArrayList<UIObject>();
		
		for(UIObject x : children()) {
			if(cls.isInstance(x))
				result.add(x);
		}
		
		return result;
	}
	
	public ArrayList<UIObject> children() {
		ArrayList<UIObject> result = new ArrayList<UIObject>();
		
		if(obj instanceof Group) {
			ArrayList<UIObject> children = ((Group)obj).getChildren();
			result.addAll(children);
			for(UIObject c : children)
				result.addAll(new Search(c, depth - 1).children());
		}
		
		return result;
	}
}
