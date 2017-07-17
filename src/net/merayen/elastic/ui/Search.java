package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

public class Search {
	private UIObject obj;

	public Search(UIObject obj) {
		this.obj = obj;
	}
	
	public UIObject getTop() { // TODO neste gang: endre mange til å være getWindow() i stedet
		UIObject top = obj;
		while(top.getParent() != null)
			top = top.getParent();
		
		return top;
	}

	/**
	 * Search downwards for a type
	 */
	public ArrayList<UIObject> childrenByType(Class<? extends UIObject> cls) {
		ArrayList<UIObject> result = new ArrayList<UIObject>();

		for(UIObject x : getAllChildren()) {
			if(cls.isInstance(x))
				result.add(x);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public <T extends UIObject> T parentByType(Class<T> cls) {
		UIObject x = obj;
		while((x = x.getParent()) != null && !cls.isAssignableFrom(x.getClass()));

		return (T)x;
	}

	@SuppressWarnings("unchecked")
	public <T> T parentByInterface(Class<T> cls) {
		UIObject x = obj;
		while((x = x.getParent()) != null && !cls.isAssignableFrom(x.getClass()));

		return (T)x;
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

	public List<UIObject> getChildren() {
		return new ArrayList<>(obj.children);
	}
}
