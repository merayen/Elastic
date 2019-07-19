package net.merayen.elastic.util.math;

import java.util.List;

import net.merayen.elastic.util.Point;

/**
 * Unfinished utility class.
 * Only a holder for the Dot-definition.
 */
public class BezierCurve {
	public static class Dot {
		public Point position = new Point();
		public Point left = new Point();
		public Point right = new Point();

		public Dot() {}

		public Dot(Point position, Point left, Point right) {
			this.position = position;
			this.left = left;
			this.right = right;
		}
	}

	/**
	 * Converts a flat list of points to an array of BezierCurve.Dot()s
	 */
	public static Dot[] fromFlat(List<Float> points) {
		if(points.size() % (2 * 3) != 0)
			throw new RuntimeException();

		Dot[] result = new Dot[points.size() / (2 * 3)];

		int p = 0;
		for(int i = 0; i < result.length; i++) {
			Dot dot = result[i] = new BezierCurve.Dot();

			dot.left.setX(points.get(p++));
			dot.left.setY(points.get(p++));

			dot.position.setX(points.get(p++));
			dot.position.setY(points.get(p++));

			dot.right.setX(points.get(p++));
			dot.right.setY(points.get(p++));
		}

		return result;
	}
}
