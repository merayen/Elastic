package net.merayen.elastic.ui.objects.top.viewport;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.views.View;

public class VerticalViewport extends UIObject {
	private List<View> views = new ArrayList<View>();

	public float width, height; // Set by parent Viewport

	@Override
	protected void onDraw() {
		
		super.onDraw();
	}
}
