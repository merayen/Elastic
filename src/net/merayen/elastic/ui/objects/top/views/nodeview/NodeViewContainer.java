package net.merayen.elastic.ui.objects.top.views.nodeview;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;

class NodeViewContainer extends UIObject {
	@Override
	public void onDraw(Draw draw) {
		draw.empty(-10000000f, -10000000f, 10000000000f, 10000000000f);
	}
}
