package net.merayen.elastic.ui.objects.components.curvebox;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.Color;
import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.util.Movable;
import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.math.BezierCurve;
import net.merayen.elastic.util.math.SignalBezierCurve;

public class BezierCurveBox extends UIObject {
	public interface Handler {
		/**
		 * Called every time a dot moves a pixel.
		 */
		public void onMove(BezierDot point);

		/**
		 * Called after user has let go of a dot.
		 */
		public void onChange();
	}

	public class BezierDot extends UIObject {
		public BezierDotDragable position = new BezierDotDragable(this);
		public BezierDotDragable left_dot = new BezierDotDragable(this);
		public BezierDotDragable right_dot = new BezierDotDragable(this);

		private BezierDot() {
			add(position);
			add(left_dot);
			add(right_dot);

			left_dot.radius = 0.03f;
			right_dot.radius = 0.03f;

			left_dot.color.red = 255;
			left_dot.color.green = 255;
			left_dot.color.blue = 200;

			right_dot.color.red = 255;
			right_dot.color.green = 255;
			right_dot.color.blue = 200;
		}
	}

	public class BezierDotDragable extends UIObject {
		public final Color color = new Color(255, 200, 0);
		public float radius = 0.05f;

		Movable movable;
		boolean visible = true; // Set to false to hide the dot (not possible to move it)

		private final BezierDot point;

		BezierDotDragable(BezierDot point) {
			this.point = point;
		}

		@Override
		protected void onInit() {
			movable = new Movable(this, this);
			movable.setHandler(new Movable.IMoveable() {
				
				@Override
				public void onMove() {
					translation.x = Math.max(0, Math.min(1, translation.x));
					translation.y = Math.max(0, Math.min(1, translation.y));
					if(handler != null)
						handler.onMove(point);
				}
				
				@Override
				public void onGrab() {}
				
				@Override
				public void onDrop() {
					handler.onChange();
				}
			});
		}

		@Override
		protected void onDraw() {
			if(visible) {
				draw.setColor(color.red, color.green, color.blue);
				draw.fillOval(-radius / 2, -radius / 2, radius, radius);
			}
		}

		@Override
		protected void onEvent(IEvent event) {
			if(visible)
				movable.handle(event);
		}
	}

	public float width = 100;
	public float height = 100;

	/**
	 * Use this object to draw stuff inside our view.
	 * NOTE: It uses a relative 0 to 1 scale!
	 */
	public final UIObject background = new UIObject();

	private final List<BezierDot> points = new ArrayList<>();
	private Handler handler;

	public BezierCurveBox() {
		initPoints();
	}

	@Override
	protected void onInit() {
		add(background, 0);
	}

	private void initPoints() {
		BezierDot start = new BezierDot();
		start.position.translation.y = 0.5f;
		start.right_dot.translation.x = 0.5f;
		start.right_dot.translation.y = 0.75f;
		start.left_dot.visible = false; // Not being used
		points.add(start);
		add(start);

		BezierDot stop = new BezierDot();
		stop.position.translation.x = 1f;
		stop.position.translation.y = 0.5f;
		stop.left_dot.translation.x = 0.5f;
		stop.left_dot.translation.y = 0.25f;
		stop.right_dot.visible = false; // Not being used

		points.add(stop);
		add(stop);
	}

	@Override
	protected void onDraw() {
		// Make our content scale to a 0 to 1 coordinate system
		translation.scale_x = 1 / width;
		translation.scale_y = 1 / height;
		translation.clip = new Rect(0, 0, width + 1, height + 1);

		draw.setColor(20, 20, 40);
		draw.fillRect(0, 0, 1, 1);

		draw.setColor(150, 150, 150);
		draw.setStroke(1 / ((width  + height) / 2));
		draw.rect(0, 0, 1, 1);

		// The curve
		draw.setColor(255, 200, 0);
		Point[] p = new Point[(points.size() - 1) * 3];

		int i = 0;
		for(int j = 1; j < points.size(); j++) {
			BezierDot before = points.get(j - 1);
			BezierDot current = points.get(j);

			p[i++] = new Point(before.right_dot.translation.x, before.right_dot.translation.y);
			p[i++] = new Point(current.left_dot.translation.x, current.left_dot.translation.y);
			p[i++] = new Point(current.position.translation.x, current.position.translation.y);
		}

		BezierDot bps = (BezierDot)points.get(0); // The initial point
		draw.bezier(bps.position.translation.x, bps.position.translation.y, p);

		// Draw lines from the dots to the points
		drawDotLines();

		//drawDiagnostics();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	private void drawDotLines() {
		draw.setStroke(1 / (width  + height));
		draw.setColor(200, 180, 0);

		for(BezierDot bp : points) {
			if(bp.left_dot.visible)
				draw.line(bp.position.translation.x, bp.position.translation.y, bp.left_dot.translation.x, bp.left_dot.translation.y);

			if(bp.right_dot.visible)
				draw.line(bp.position.translation.x, bp.position.translation.y, bp.right_dot.translation.x, bp.right_dot.translation.y);
		}
	}

	public BezierDot insertPoint(int before_index) {
		if(before_index < 1)
			throw new RuntimeException("Can not insert before the first point as it is fixed");

		if(before_index > points.size() - 1)
			throw new RuntimeException("Can not add after the last point as it is fixed");

		BezierDot bpa = new BezierDot();

		points.add(before_index, bpa);
		add(bpa);

		return bpa;
	}

	public BezierDot getBezierPoint(int index) {
		return points.get(index);
	}

	public BezierDot[] getBezierPoints() {
		return points.toArray(new BezierDot[points.size()]);
	}

	public int getIndex(BezierDot point) {
		return points.indexOf(point);
	}

	public int getPointCount() {
		return points.size();
	}

	/**
	 * Gets all points as a flat list of floats in this format: [p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, ...]
	 * @return
	 */
	public List<Number> getFloats() {
		List<Number> result = new ArrayList<Number>();

		int i = 0;
		for(BezierDot bd : points) {
			result.add(bd.left_dot.translation.x);
			result.add(bd.left_dot.translation.y);

			result.add(bd.position.translation.x);
			result.add(bd.position.translation.y);

			result.add(bd.right_dot.translation.x);
			result.add(bd.right_dot.translation.y);
		}

		return result;
	}

	public void setPoints(List<Number> new_points) {
		if(new_points.size() % 6 != 0)
			throw new RuntimeException("Invalid point length");

		clearPoints();

		int i = 0;
		while(i < new_points.size()) {
			BezierDot dot = new BezierDot();
			dot.left_dot.translation.x = new_points.get(i++).floatValue();
			dot.left_dot.translation.y = new_points.get(i++).floatValue();
			dot.position.translation.x = new_points.get(i++).floatValue();
			dot.position.translation.y = new_points.get(i++).floatValue();
			dot.right_dot.translation.x = new_points.get(i++).floatValue();
			dot.right_dot.translation.y = new_points.get(i++).floatValue();

			points.add(dot);
			add(dot);
		}
	}

	private void clearPoints() {
		for(UIObject o : points)
			remove(o);

		points.clear();
	}

	private void drawDiagnostics() {
		BezierCurve.Dot[] dots = BezierCurve.fromFlat(getFloats());
		float[] result = new float[1000];
		SignalBezierCurve.getValues(dots, result);

		draw.setColor(0, 255, 255);
		for(int i = 0; i < result.length; i++)
			draw.fillOval(i / (float)result.length - 0.005f, result[i] - 0.005f, 0.01f, 0.01f);
	}
}
