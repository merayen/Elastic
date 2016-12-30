package net.merayen.elastic.util.math;

import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.math.BezierCurve.Dot;

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
	public static Dot[] fromFlat(float[] points) {
		if(points.length % (2 * 3) != 0)
			throw new RuntimeException();

		Dot[] result = new Dot[points.length / (2 * 3)];

		int p = 0;
		for(int i = 0; i < result.length; i++) {
			Dot dot = result[i] = new BezierCurve.Dot();

			dot.left.x = points[p++];
			dot.left.y = points[p++];

			dot.position.x = points[p++];
			dot.position.y = points[p++];

			dot.right.x = points[p++];
			dot.right.y = points[p++];
		}

		return result;
	}
}
