package net.merayen.elastic.ui.objects.components.curvebox;

import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox.BezierDot;
import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.math.BezierCurve;
import net.merayen.elastic.util.math.SignalBezierCurve;

/**
 * Bezier curve for shaping signals.
 * It limits how the curve can be managed.
 */
public class SignalBezierCurveBox extends UIObject { // Move out from test_100
	public interface Handler {
		/**
		 * Called when user has changed the curve.
		 */
		public void onChange();

		/**
		 * Called very often when user is changing something.
		 */
		public void onMove();
	}

	private class Overlay extends UIObject {
		@Override
		protected void onDraw() {
			draw.setColor(100, 100, 100);
			draw.setStroke(0.01f);
			draw.line(0, offset, 1, offset);
		}
	}

	public float width = 100;
	public float height = 100;

	private final BezierCurveBox curve = new BezierCurveBox();
	private float offset;
	private Handler handler;

	private boolean moving; // Don't accept new points when user is interacting with us

	public SignalBezierCurveBox() {
		// We do not allow user moving the start and stop point
		curve.getBezierPoint(0).position.visible = false;
		((BezierDot)curve.getBezierPoint(1)).position.visible = false;
	}

	@Override
	protected void onInit() {
		curve.setHandler(new BezierCurveBox.Handler() {
			@Override
			public void onMove(BezierDot point) {
				moving = true;

				constrainPoint(point);
				offset = getOffset();

				if(handler != null)
					handler.onMove();
			}

			@Override
			public void onChange() {
				moving = false;
				if(handler != null)
					handler.onChange();
			}
		});

		add(curve);

		//insertPoint(1);
		//insertPoint(1);

		curve.background.add(new Overlay());

		offset = getOffset();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public BezierDot insertPoint(int index) {
		BezierDot before = curve.getBezierPoint(index - 1);
		BezierDot after = curve.getBezierPoint(index);

		BezierDot bpa = curve.insertPoint(1);

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
		for(BezierDot bp : curve.getBezierPoints())
			constrainPoint(bp);
	}

	private void constrainPoint(BezierDot point) {
		int index = curve.getIndex(point);

		// Constrain X-axis for both handles on the point, to the points around
		if(index > 0) {
			BezierDot before = curve.getBezierPoint(index - 1);
			if(point.left_dot.translation.x < before.position.translation.x)
				point.left_dot.translation.x = before.position.translation.x;

			if(point.position.translation.x < before.position.translation.x)
				point.position.translation.x = before.position.translation.x;

			if(point.position.translation.x < before.right_dot.translation.x)
				point.position.translation.x = before.right_dot.translation.x;
		}

		if(index < curve.getPointCount() - 1) {
			BezierDot after = curve.getBezierPoint(index + 1);

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
	}

	@Override
	protected void onUpdate() {
		curve.width = width;
		curve.height = height;
	}

	private final static int OFFSET_LINE_RESOLUTION = 100;

	public float getOffset() {
		float[] data = new float[OFFSET_LINE_RESOLUTION];
		SignalBezierCurve.getValues(getDots(), data);

		double result = 0;
		for(float f : data)
			result += f;

		return (float)(result / OFFSET_LINE_RESOLUTION);
	}

	public void setPoints(List<Number> new_points) {
		if(moving)
			return; // Don't accept new points when user is interacting with us

		curve.setPoints(new_points);

		BezierCurveBox.BezierDot start = curve.getBezierPoint(0);
		start.left_dot.visible = false;
		start.position.visible = false;
		start.position.translation.x = 0;
		start.position.translation.y = 0.5f;

		BezierCurveBox.BezierDot stop = curve.getBezierPoint(curve.getPointCount() - 1);
		stop.position.visible = false;
		stop.right_dot.visible = false;
		stop.position.translation.x = 1;
		stop.position.translation.y = 0.5f;
	}

	public BezierCurve.Dot[] getDots() {
		BezierCurveBox.BezierDot[] points = curve.getBezierPoints();
		BezierCurve.Dot[] result = new BezierCurve.Dot[points.length];

		for(int i = 0; i < points.length; i++) {
			BezierCurveBox.BezierDot bp = points[i];
			result[i] = new BezierCurve.Dot(
				new Point(bp.position.translation.x, bp.position.translation.y),
				new Point(bp.left_dot.translation.x, bp.left_dot.translation.y),
				new Point(bp.right_dot.translation.x, bp.right_dot.translation.y)
			);
		}

		return result;
	}

	public List<Number> getFloats() {
		return curve.getFloats();
	}
}
