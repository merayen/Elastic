package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.uinodes.list.test_100.BezierCurveBox.BezierPoint;

public class BezierWaveBox extends UIObject {
	private final BezierCurveBox curve = new BezierCurveBox();

	@Override
	protected void onInit() {
		curve.setHandler(new BezierCurveBox.Handler() {
			@Override
			public void onMove(BezierPoint point) {
				constrainPoint(point);
			}
		});

		add(curve);

		// We do not allow user moving the start and stop point
		curve.getPoint(0).position.visible = false;
		((BezierPoint)curve.getPoint(1)).position.visible = false;

		insertPoint(1);
		insertPoint(1);
	}

	public BezierPoint insertPoint(int index) {
		BezierPoint before = curve.getPoint(index - 1);
		BezierPoint after = curve.getPoint(index);

		BezierPoint bpa = curve.insertPoint(1);

		// Come up with a position for the new point XXX fix, this sucks
		bpa.position.translation.x = (before.position.translation.x + after.position.translation.x) / 2;
		bpa.position.translation.y = (before.position.translation.y + after.position.translation.y) / 2;

		bpa.right_dot.translation.x = bpa.position.translation.x + 0.04f;
		bpa.right_dot.translation.y = bpa.position.translation.y + 0.04f;

		bpa.left_dot.translation.x = bpa.position.translation.x - 0.04f;
		bpa.left_dot.translation.y = bpa.position.translation.y - 0.04f;

		constrainAllPoints();

		return bpa;
	}

	/**
	 * Makes sure all points are valid. Do this if you have manually edited any points.
	 * This ensures that the bezier doesn't go back in X-axis.
	 */
	public void constrainAllPoints() {
		for(BezierPoint bp : curve.getPoints())
			constrainPoint(bp);
	}

	private void constrainPoint(BezierPoint point) {
		int index = curve.getIndex(point);

		// Constrain X-axis for both handles on the point, to the points around
		if(index > 0) {
			BezierPoint before = curve.getPoint(index - 1);
			if(point.left_dot.translation.x < before.position.translation.x)
				point.left_dot.translation.x = before.position.translation.x;

			if(point.position.translation.x < before.position.translation.x)
				point.position.translation.x = before.position.translation.x;

			if(point.position.translation.x < before.right_dot.translation.x)
				point.position.translation.x = before.right_dot.translation.x;
		}

		if(index < curve.getPointCount() - 1) {
			BezierPoint after = curve.getPoint(index + 1);

			if(point.right_dot.translation.x > after.position.translation.x)
				point.right_dot.translation.x = after.position.translation.x;

			if(point.position.translation.x > after.position.translation.x)
				point.position.translation.x = after.position.translation.x;

			if(point.position.translation.x > after.left_dot.translation.x)
				point.position.translation.x = after.left_dot.translation.x;
		}

		if(point.right_dot.translation.x < point.position.translation.x)
			point.right_dot.translation.x = point.position.translation.x;

		if(point.left_dot.translation.x > point.position.translation.x)
			point.left_dot.translation.x = point.position.translation.x;

		/*if(index < curve.getPointCount() - 1) {
			if(point.right_dot.translation.x < before.position.translation.x)
				point.right_dot.translation.x = before.position.translation.x;
		}*/
	}
}
