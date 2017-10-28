package net.merayen.elastic.ui.objects.top.views.arrangementview;

import net.merayen.elastic.ui.UIObject;

class TrackBody extends UIObject {
	float width, height;

	@Override
	protected void onDraw() {
		draw.setColor(20, 20, 50);
		draw.fillRect(2, 2, width - 4, height - 4);
	}
}
