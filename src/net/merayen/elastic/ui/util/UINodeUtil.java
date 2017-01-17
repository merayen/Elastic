package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.NodeGroupInitiator;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.objects.top.Window;

public class UINodeUtil {
	private UINodeUtil() {}

	/**
	 * Retrieves the Top() UIObject the UIObject is a child of. 
	 */
	public static Window getWindow(UIObject uiobject) {
		return (Window)uiobject.search.parentByType(Window.class);
	}

	public static Top getTop(UIObject uiobject) {
		return (Top)uiobject.search.getTop();
	}

	/**
	 * Figure out which Node-group this UIObject is within.
	 * This requires a parent UIObject somewhere that implements NodeGroupInitiator()
	 */
	public static String getGroup(UIObject uiobject) {
		NodeGroupInitiator obj = uiobject.search.parentByInterface(NodeGroupInitiator.class);

		if(obj == null)
			throw new RuntimeException("No parent with NodeGroupInitiator implemented");

		return obj.getGroup();
	}
}
