package net.merayen.elastic.uinodes.list.test_100;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.Color;
import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.util.Movable;
import net.merayen.elastic.util.Point;

public class BezierCurveBox extends UIObject {
	public interface Handler {
		public void onMove(BezierDot point);
	}

	public class BezierDot extends UIObject {
		public BezierDotDragable position = new BezierDotDragable(this);
		public BezierDotDragable left_dot = new BezierDotDragable(this);
		public BezierDotDragable right_dot = new BezierDotDragable(this);

		private BezierDot() {}

		@Override
		protected void onInit() {
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
					if(handler != null)
						handler.onMove(point);
				}
				
				@Override
				public void onGrab() {}
				
				@Override
				public void onDrop() {}
			});
		}

		@Override
		protected void onDraw() {
			// Keep values inside the box
			translation.x = Math.max(0, Math.min(1, translation.x));
			translation.y = Math.max(0, Math.min(1, translation.y));

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
		// Make our content scale to a 0 to 1 coordinate system
		translation.scale_x = 1 / width;
		translation.scale_y = 1 / height;

		translation.clip = new Rect(0, 0, width, height);

		add(background, true);
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
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	private void drawDotLines() {
		draw.setStroke(1 / (width  + height));
		draw.setColor(200, 180, 0);

		for(BezierDot bp : points) {
			/*if(i == 0) {
				BezierPoint bps = (BezierPoint)points.get(0);
				BezierPoint bpa = (BezierPoint)points.get(1);
				draw.line(bps.position.translation.x, bps.position.translation.y, bpa.left_dot.translation.x, bpa.left_dot.translation.y);
			} else {
				BezierPoint bpa = (BezierPoint)points.get(i);
				BezierPoint before = points.get(i - 1);
				//draw.line(before.position.translation.x, before.position.translation.y, bpa.left_dot.translation.x, bpa.left_dot.translation.y);
				//draw.line(bpa.position.translation.x, bpa.position.translation.y, bpa.right_dot.translation.x, bpa.right_dot.translation.y);
			}*/
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
}
