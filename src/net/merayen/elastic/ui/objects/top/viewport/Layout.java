package net.merayen.elastic.ui.objects.top.viewport;

import java.util.List;

class Layout {
	class LayoutObj {
		float width, height;

		Viewport viewport;
		// ...or:
		List<LayoutObj> children;
	}

	class Horizontal extends LayoutObj {
		
		// or:
	}

	class Vertical extends LayoutObj {
		List<LayoutObj> children;
	}

	Layout() {
		
	}

	void addHorizontal(Viewport viewport, Viewport parent) {
		
	}

	void addVertical(Viewport viewport, Viewport parent) {
		
	}

	void remove(Viewport viewport) {
		LayoutObj l = getLayoutObj(viewport);
		
	}

	void updateLayout(float width, float height) {
		
	}

	private LayoutObj getLayoutObj(Viewport viewport) {
		
	}
}