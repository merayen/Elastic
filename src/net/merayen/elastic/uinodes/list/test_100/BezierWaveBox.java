package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.uinodes.list.test_100.BezierCurveBox.BezierPoint;
import net.merayen.elastic.uinodes.list.test_100.BezierCurveBox.BezierPointAfter;

public class BezierWaveBox extends UIObject {
	private final BezierCurveBox curve = new BezierCurveBox();

	@Override
	protected void onInit() {
		curve.setHandler(new BezierCurveBox.Handler() {
			@Override
			public void onMove(BezierPoint point) {
				int index = curve.getIndex(point);

				if(index == 0)
					throw new RuntimeException("Should not happen - start point should not be visible");

				// Constrain X-axis for both handles on the point, to the points around
				//point.p0.translation.x = (float)Math.max(0.2, point.p0.translation.x);
			}
		});

		add(curve);

		// We do not allow user moving the start and stop point
		curve.getPoint(0).position.visible = false;
		((BezierPointAfter)curve.getPoint(1)).position.visible = false;

		insertPoint(1);
	}

	public BezierPointAfter insertPoint(int index) {
		BezierPoint before = (BezierPoint)curve.getPoint(index - 1);
		BezierPointAfter after = (BezierPointAfter)curve.getPoint(index);

		BezierPointAfter bpa = curve.insertPoint(1);

		// Come up with a position for the new point XXX fix, this sucks
		//float a = bpa.position.translation.x = (before.position.translation.x + after.position.translation.x) / 2;
		//float b = bpa.position.translation.y = (before.position.translation.y + after.position.translation.y) / 2;

		/*float c = bpa.right_dot.translation.x = bpa.position.translation.x - 0.01f;
		float d = bpa.right_dot.translation.y = bpa.position.translation.y - 0.01f;

		float e = bpa.left_dot.translation.x = bpa.position.translation.x + 0.01f;
		float f = bpa.left_dot.translation.y = bpa.position.translation.y + 0.01f;*/

		return bpa;
	}
}
