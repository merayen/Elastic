package net.merayen.elastic.ui.objects.components.curvebox;

import java.util.List;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox.BezierDot;
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox.BezierDotDragable;
import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.math.BezierCurve;
import net.merayen.elastic.util.math.SignalBezierCurve;

/**
 * Bezier curve for shaping signals.
 * It limits how the curve can be managed.
 */
public class SignalBezierCurveBox extends UIObject implements BezierCurveBoxInterface { // Move out from test_100
	public interface Handler {
		/**
		 * Called when user has changed the curve.
		 */
		public void onChange();

		/**
		 * Called very often when user is changing something.
		 */
		public void onMove();

		/**
		 * When user clicks down on a dot.
		 */
		public void onDotClick();
	}

	private class Overlay extends UIObject {
		@Override
		public void onDraw(Draw draw) {
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
	public void onInit() {
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

			@Override
			public void onSelect(BezierDotDragable dot) {
				dot.color.setRed(40);
			}
		});

		add(curve);

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
		bpa.position.getTranslation().x = (before.position.getTranslation().x + after.position.getTranslation().x) / 2;
		bpa.position.getTranslation().y = (before.position.getTranslation().y + after.position.getTranslation().y) / 2;

		bpa.right_dot.getTranslation().x = bpa.position.getTranslation().x + 0.04f;
		bpa.right_dot.getTranslation().y = bpa.position.getTranslation().y + 0.04f;

		bpa.left_dot.getTranslation().x = bpa.position.getTranslation().x - 0.04f;
		bpa.left_dot.getTranslation().y = bpa.position.getTranslation().y - 0.04f;

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
			if(point.left_dot.getTranslation().x < before.position.getTranslation().x)
				point.left_dot.getTranslation().x = before.position.getTranslation().x;

			if(point.position.getTranslation().x < before.position.getTranslation().x)
				point.position.getTranslation().x = before.position.getTranslation().x;

			if(point.position.getTranslation().x < before.right_dot.getTranslation().x)
				point.position.getTranslation().x = before.right_dot.getTranslation().x;
		}

		if(index < curve.getPointCount() - 1) {
			BezierDot after = curve.getBezierPoint(index + 1);

			if(point.right_dot.getTranslation().x > after.position.getTranslation().x)
				point.right_dot.getTranslation().x = after.position.getTranslation().x;

			if(point.position.getTranslation().x > after.position.getTranslation().x)
				point.position.getTranslation().x = after.position.getTranslation().x;

			if(point.position.getTranslation().x > after.left_dot.getTranslation().x)
				point.position.getTranslation().x = after.left_dot.getTranslation().x;
		}

		if(point.right_dot.getTranslation().x < point.position.getTranslation().x)
			point.right_dot.getTranslation().x = point.position.getTranslation().x;

		if(point.left_dot.getTranslation().x > point.position.getTranslation().x)
			point.left_dot.getTranslation().x = point.position.getTranslation().x;
	}

	@Override
	public void onUpdate() {
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
		start.position.getTranslation().x = 0;
		start.position.getTranslation().y = 0.5f;

		BezierCurveBox.BezierDot stop = curve.getBezierPoint(curve.getPointCount() - 1);
		stop.position.visible = false;
		stop.right_dot.visible = false;
		stop.position.getTranslation().x = 1;
		stop.position.getTranslation().y = 0.5f;
	}

	public BezierCurve.Dot[] getDots() {
		BezierCurveBox.BezierDot[] points = curve.getBezierPoints();
		BezierCurve.Dot[] result = new BezierCurve.Dot[points.length];

		for(int i = 0; i < points.length; i++) {
			BezierCurveBox.BezierDot bp = points[i];
			result[i] = new BezierCurve.Dot(
				new Point(bp.position.getTranslation().x, bp.position.getTranslation().y),
				new Point(bp.left_dot.getTranslation().x, bp.left_dot.getTranslation().y),
				new Point(bp.right_dot.getTranslation().x, bp.right_dot.getTranslation().y)
			);
		}

		return result;
	}

	public List<Number> getFloats() {
		return curve.getFloats();
	}
}
