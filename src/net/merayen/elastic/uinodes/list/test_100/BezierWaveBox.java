package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.uinodes.list.test_100.BezierCurveBox.BezierPoint;

public class BezierWaveBox extends UIObject { // Move out from test_100
	public final BezierCurveBox curve = new BezierCurveBox();

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

		//insertPoint(1);
		//insertPoint(1);
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

	@Override
	protected void onDraw() {
		draw.setColor(255, 255, 255);
		for(float i = 0; i < 1; i+=0.01f) {
			float x = getValue(0, i, false);
			float y = getValue(0, i, true);
			draw.fillOval(x * 100, -100 + y * 100, 2, 2);
		}

		// de Casteljau's algorithm
		deCasteljau();
	}

	private void deCasteljau() {
		draw.setColor(200, 200, 255);
		float[] x_points = new float[curve.getPointCount() * 3 - 2];
		float[] y_points = new float[curve.getPointCount() * 3 - 2];

		int i = 0;
		int ip = 0;
		for(BezierPoint p : curve.getPoints()) {
			x_points[ip] = p.position.translation.x;
			y_points[ip] = p.position.translation.y;
			ip++;

			if(i > 0) {
				x_points[ip] = p.left_dot.translation.x;
				y_points[ip] = p.left_dot.translation.y;
				ip++;
			}

			if(i != curve.getPointCount() - 1) {
				x_points[ip] = p.right_dot.translation.x;
				y_points[ip] = p.right_dot.translation.y;
				ip++;
			}

			i++;
		}

		System.out.println(workIt(y_points, 0.9f));
	}

	/**
	 * Changes the input array.
	 */
	private float workIt(final float[] points, float t) {
		for(int stop = points.length - 2; stop > 1; stop--) {
			for(int i = 0; i < stop; i++)
				points[i] = (1 - t) * points[i] + t * points[i+i];
		}

		return points[0];
	}

	private float getValue(int index, float t, boolean y) {
		if(index > curve.getPointCount() - 1)
			throw new RuntimeException("Can not calculate from last point");

		BezierPoint current = curve.getPoint(index);
		BezierPoint after = curve.getPoint(index + 1);

		if(!y)
			return getAxis(
				new float[]{
					current.position.translation.x,
					current.right_dot.translation.x,
					after.left_dot.translation.x,
					after.position.translation.x
				},
				t
			);

		return getAxis(
			new float[]{
				current.position.translation.y,
				current.right_dot.translation.y,
				after.left_dot.translation.y,
				after.position.translation.y
			},
			t
		);
	}

	private float getAxis(float[] p, float t) {
		float one = (float)(Math.pow(1 - t, 3) * p[0]);
		float two = (float)(3 * Math.pow(1 - t, 2) * t * p[1]);
		float three = (float)(3 * (1 - t) * Math.pow(t, 2) * p[2]);
		float four = (float)(Math.pow(t, 3) * p[3]);
		
		return one + two + three + four;
	}
}
