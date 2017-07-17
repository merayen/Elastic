package net.merayen.elastic.ui.objects.components.autolayout;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.util.Point;

public class LayoutMethods {
	public static AutoLayout.Placement horizontal(float margin) {
		return (int index, UIObject previous, UIObject current) -> new Point(
			margin + (previous != null ? previous.getOutline().getWidth() : 0), 0
		);
	}

	public static AutoLayout.Placement vertical(float margin) {
		return (int index, UIObject previous, UIObject current) -> new Point(
			0, margin + (previous != null ? previous.getOutline().getHeight() : 0)
		);
	}
}
