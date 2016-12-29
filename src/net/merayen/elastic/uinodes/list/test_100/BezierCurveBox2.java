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

public class BezierCurveBox2 extends UIObject {
	private class BezierPoint extends UIObject {

		Dot p0 = new Dot();
		Dot p1 = new Dot();
		Dot p2 = new Dot();

		@Override
		protected void onInit() {
			add(p0);
			add(p1);
			add(p2);
		}
	}

	private class Dot extends UIObject {
		final Color color = new Color();
		MouseHandler movable;
		boolean visible; // Set to true to make it movable

		@Override
		protected void onInit() {
			movable = new Movable(this, this);
		}

		@Override
		protected void onDraw() {
			final float radius = 0.05f;

			if(visible)
				draw.fillOval(-radius / 2, -radius / 2, radius, radius);
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
		super.onInit();

		//add(clip);
		// Make our content scale to a 0 to 1 coordinate system
		translation.scale_x = 1 / width;
		translation.scale_y = 1 / height;

		translation.clip = new Rect(0, 0, width, height);

		BezierPoint stop = new BezierPoint();
		stop.p0.translation.x = 0.5f;
		stop.p0.translation.y = 1;
		stop.p1.translation.x = 0.5f;
		stop.p1.translation.y = 0;
		stop.p2.translation.x = 1;
		stop.p2.translation.y = 0.5f;

		stop.p0.visible = true;
		stop.p1.visible = true;

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
		Point[] p = new Point[points.size() * 3];

		int i = 0;
		for(BezierPoint bp : points) {
			p[i  ] = new Point(bp.p0.translation.x, bp.p0.translation.y);
			p[i+1] = new Point(bp.p1.translation.x, bp.p1.translation.y);
			p[i+2] = new Point(bp.p2.translation.x, bp.p2.translation.y);

			i++;
		}

		draw.bezier(0, 0.5f, p);

		// Draw lines from the dots to the points
		drawDotLines();
	}

	private void drawDotLines() {
		draw.setStroke(0.01f);
		draw.setColor(255, 200, 0);
		
		for(int i = 0; i < points.size(); i++) {
			BezierPoint bp = points.get(i);
			draw.line(bp.p1.translation.x, bp.p1.translation.y, bp.p2.translation.x, bp.p2.translation.y);
		}
	}

	@Override
	protected void onUpdate() {
		
	}
}
