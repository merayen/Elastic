package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.ui.UIObject;

public class BezierWaveBox extends UIObject {
	private final BezierCurveBox curve = new BezierCurveBox();

	@Override
	protected void onInit() {
		add(curve);
	}
}
