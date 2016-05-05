package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

public class Search {
	private UIObject obj;
	private int depth;

	Search(UIObject obj) {
		this(obj, 1000);
	}

	public Search(UIObject obj, int depth) {
		this.obj = obj;
		this.depth = depth;
	}
	
	/*public static Search getTopSearch(UIObject obj) {
		return 
	}*/
	
	public UIObject getTop() {
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

		for(UIObject x : getAllChildren()) {
			if(cls.isInstance(x))
				result.add(x);
		}

		return result;
	}
	
	public List<UIObject> getAllChildren() { // Not tested
		List<UIObject> result = new ArrayList<>();
		List<UIObject> stack = new ArrayList<>();

		stack.add(obj);

		while(stack.size() > 0) {
			UIObject current = stack.remove(0);
			for(UIObject o : current.children) {
				result.add(o);
				stack.add(o);
			}
		}

		return result;
	}
}
