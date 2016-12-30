package net.merayen.elastic.util.math;

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
}
