package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.util.Point;

public class HitTester {
	public static boolean inside(Point point, Rect rect) {
		return inside(point.getX(), point.getY(), rect.getX1(), rect.getY1(), rect.getX2(), rect.getY2());
	}

	public static boolean inside(float px, float py, float x1, float y1, float x2, float y2) {
		return
			px >= x1 && py >= y1 &&
			px < x2  && py < y2;
	}
}
