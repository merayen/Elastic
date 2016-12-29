package net.merayen.elastic.uinodes.list.test_100;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.Color;
import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.ui.util.Movable;

public class BezierCurveBox extends UIObject {
	private class BezierPoint extends UIObject {
		Dot p0 = new Dot();

		@Override
		protected void onInit() {
			add(p0);
		}
	}

	private class BezierPointAfter extends BezierPoint {
		Dot p1 = new Dot();
		Dot p2 = new Dot();

		@Override
		protected void onInit() {
			super.onInit();
			add(p1);
			add(p2);
		}
	}

	private class Dot extends UIObject {
		MouseHandler movable;
		boolean visible = true; // Set to false to hide the dot (not possible to move it)

		@Override
		protected void onInit() {
			movable = new Movable(this, this);
		}

		@Override
		protected void onDraw() {
			if(visible) {
				final float radius = 0.05f;
				draw.setColor(255, 200, 0);
				draw.fillOval(-radius / 2, -radius / 2, radius, radius);
			}
		}

		@Override
		protected void onEvent(IEvent event) {
			if(visible)
				movable.handle(event);

			translation.x = Math.max(0, Math.min(1, translation.x));
			translation.y = Math.max(0, Math.min(1, translation.y));
		}
	}

	public float width = 100;
	public float height = 100;

	private final List<BezierPoint> points = new ArrayList<>();

	@Override
	protected void onInit() {
		// Make our content scale to a 0 to 1 coordinate system
		translation.scale_x = 1 / width;
		translation.scale_y = 1 / height;

		translation.clip = new Rect(0, 0, width, height);

		initPoints();
	}

	private void initPoints() {
		BezierPoint start = new BezierPoint();
		start.p0.translation.y = 0.5f;
		points.add(start);
		add(start);

		BezierPointAfter stop = new BezierPointAfter();
		stop.p0.translation.x = 0.5f;
		stop.p0.translation.y = 1;
		stop.p1.translation.x = 0.5f;
		stop.p1.translation.y = 0;
		stop.p2.translation.x = 1;
		stop.p2.translation.y = 0.5f;

		//stop.p0.visible = true;
		//stop.p1.visible = true;

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

		int i = -1;
		for(BezierPoint bp : points) {
			if(i > -1) { // First one is special and is handled below
				BezierPointAfter bpa = (BezierPointAfter)bp;
				p[i  ] = new Point(bpa.p0.translation.x, bpa.p0.translation.y);
				p[i+1] = new Point(bpa.p1.translation.x, bpa.p1.translation.y);
				p[i+2] = new Point(bpa.p2.translation.x, bpa.p2.translation.y);
			}
			i++;
		}

		BezierPoint bps = (BezierPoint)points.get(0);
		draw.bezier(bps.p0.translation.x, bps.p0.translation.y, p);

		// Draw lines from the dots to the points
		drawDotLines();
	}

	private void drawDotLines() {
		draw.setStroke(1 / (width  + height));
		draw.setColor(200, 180, 0);

		for(int i = 0; i < points.size(); i++) {
			if(i == 0) {
				BezierPoint bps = (BezierPoint)points.get(0);
				BezierPoint bpa = (BezierPoint)points.get(1);
				draw.line(bps.p0.translation.x, bps.p0.translation.y, bpa.p0.translation.x, bpa.p0.translation.y);
			} else {
				BezierPointAfter bpa = (BezierPointAfter)points.get(i);
				draw.line(bpa.p1.translation.x, bpa.p1.translation.y, bpa.p2.translation.x, bpa.p2.translation.y);
			}
		}
	}

	public BezierPointAfter insertPoint(int before_index) {
		if(before_index < 1)
			throw new RuntimeException("Can not insert before the first point as it is fixed");

		if(before_index > points.size() - 1)
			throw new RuntimeException("Point does not exist");

		BezierPointAfter bpa = new BezierPointAfter();

		BezierPoint before = (BezierPointAfter)points.get(before_index - 1);
		BezierPointAfter after = (BezierPointAfter)points.get(before_index);

		bpa.p0.translation.x = (float)(before.p0.translation.x + (after.p0.translation.x - before.p0.translation.x));

		return bpa;
	}

	public BezierPoint getPoint(int index) {
		return points.get(index);
	}
}
