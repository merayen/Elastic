package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.util.Point;

public class HitTester {
	public static boolean inside(Point point, Rect rect) {
		return inside(point.x, point.y, rect.x1, rect.y1, rect.x2, rect.y2);
	}

	public static boolean inside(float px, float py, float x1, float y1, float x2, float y2) {
		return 
			px >= x1 && py >= y1 &&
			px < x2  && py < y2;
	}
}
