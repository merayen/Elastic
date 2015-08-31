package net.merayen.merasynth.ui.objects.components.graph;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.UIGroup;

public class Graph extends UIGroup {
	public class Segment {
		// X-axis is time (seconds) or BPM
		// Y-axis is value
		public Point curve_a = new Point();
		public Point curve_b = new Point();
		public Point end = new Point();
	}

	public float width = 20f;
	public float height = 20f;

	float m;

	private ArrayList<Segment> segments;

	@Override
	protected void onInit() {
		segments = new ArrayList<Segment>();

		// Default points (start and end)
		Segment s = new Segment();
		s.curve_a.x = 0;
		s.curve_a.y = 0;
		s.curve_b.x = 0;
		s.curve_b.y = 0;
		s.end.x = 0;
		s.end.y = 0;
		segments.add(s);

		int steps = 4;
		float end = 20f;
		for(int i = 1; i < steps; i++) { // DEBUG
			s = new Segment();
			s.curve_a.x = (end / steps) * (i - 0.5f);
			s.curve_a.y = 0;
			s.curve_b.x = (end / steps) * (i - 0.5f);
			s.curve_b.y = 0;
			s.end.x = (end / steps) * i;
			s.end.y = 0;
			segments.add(s);
		}

		s = new Segment();
		s.curve_a.x = 20;
		s.curve_a.y = 0;
		s.curve_b.x = 20;
		s.curve_b.y = 0;
		s.end.x = 20;
		s.end.y = 0;
		segments.add(s);
	}

	@Override
	protected void onDraw() {
		m += 0.05f;
		/*float x = (float)(Math.sin(m) * 2.5 + 2.5);
		segments.get(1).curve_a.x = x;
		segments.get(1).curve_a.y = x;
		segments.get(1).curve_b.x = x;
		segments.get(1).curve_b.y = x;

		segments.get(2).curve_a.x = x + 5;
		segments.get(2).curve_a.y = x;
		segments.get(2).curve_b.x = x + 5;
		segments.get(2).curve_b.y = x;*/

		for(int i = 2; i < /*segments.size() - 1*/ 3; i++) {
			float x = (float)Math.sin(m);
			//segments.get(i).curve_a.x = 5 * i;
			segments.get(i - 1).curve_b.y = x * 5;
			//segments.get(i + 1).curve_b.x = 5 * i;
			segments.get(i).curve_a.y = -x * 5;
			//break;
		}

		/*segments.get(2).curve_a.x = ;
		segments.get(2).curve_a.y = x;
		segments.get(2).curve_b.x = x + 5;
		segments.get(2).curve_b.y = x;*/

		draw.setColor(50, 50, 100);
		draw.fillRect(0f, 0f, width, height);

		Path2D.Float f = new Path2D.Float();

		Point2D m = this.getAbsolutePixelPoint(0, 0);
		f.moveTo(m.getX(), m.getY());

		for(Segment s : segments) {
			// Main line
			Point2D p1 = this.getAbsolutePixelPoint((float)s.curve_a.x, (float)s.curve_a.y);
			Point2D p2 = this.getAbsolutePixelPoint((float)s.curve_b.x, (float)s.curve_b.y);
			Point2D p3 = this.getAbsolutePixelPoint((float)s.end.x, (float)s.end.y);
			f.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
		}

		draw.setStroke(0.2f);
		draw.setColor(150, 150, 255);
		draw.g2d.draw(f);

		// Curve control lines
		draw.setStroke(0.1f);
		draw.setColor(200, 200, 250);
		for(Segment s : segments) {
			
			draw.line(s.end.x, s.end.y, s.curve_a.x, s.curve_a.y);
			draw.fillOval(s.curve_a.x - 0.2f, s.curve_a.y - 0.2f, .4f, .4f);
			
			draw.line(s.end.x, s.end.y, s.curve_b.x, s.curve_b.y);
			draw.fillOval(s.curve_b.x - 0.2f, s.curve_b.y - 0.2f, .4f, .4f);
		}

		super.onDraw();
	}

	private void drawPath() {
		
	}
}
