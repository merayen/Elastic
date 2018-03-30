package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.objects.top.Window;

public class UINodeUtil {
	private UINodeUtil() {}

	/**
	 * Retrieves the Top() UIObject the UIObject is a child of. 
	 */
	public static Window getWindow(UIObject uiobject) {
		return (Window) uiobject.getSearch().parentByType(Window.class);
	}

	public static Top getTop(UIObject uiobject) {
		UIObject result = uiobject.getSearch().getTop();
		if(result instanceof Top)
			return (Top)result;
		else
			return null;
	}
}
