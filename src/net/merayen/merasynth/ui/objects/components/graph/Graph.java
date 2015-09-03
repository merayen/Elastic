package net.merayen.merasynth.ui.objects.components.graph;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.UIGroup;

public class Graph extends UIGroup {
	public class Segment {
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

		for(int i = 1; i < /*segments.size() - 1*/ 2; i++) {
			float x = (float)Math.sin(m);
			segments.get(i).curve_b.y = -x * 50;
			segments.get(i + 1).curve_a.y = x * 50;
		}

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
		for(int i = 1; i < /*segments.size() - 1*/ 2; i++) {
			Segment s0 = segments.get(i);
			Segment s1 = segments.get(i + 1);

			draw.setColor(255, 0, 255);
			draw.line(s0.end.x, s0.end.y, s0.curve_b.x, s0.curve_b.y);
			draw.fillOval(s0.curve_b.x - 0.2f, s0.curve_b.y - 0.2f, .4f, .4f);

			draw.setColor(255, 255, 0);
			draw.line(s0.end.x, s0.end.y, s1.curve_a.x, s1.curve_a.y);
			draw.fillOval(s1.curve_a.x - 0.2f, s1.curve_a.y - 0.2f, .4f, .4f);
		}

		draw.debug();

		System.out.printf("%s   %s  [%f,%f]\n", this.translation.clip, this.absolute_translation.clip, this.absolute_translation.x, this.absolute_translation.x); // TODO få den til å faktisk vise clip
		super.onDraw();
	}
}
