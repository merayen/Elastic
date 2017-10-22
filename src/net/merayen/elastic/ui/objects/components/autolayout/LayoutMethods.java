package net.merayen.elastic.ui.objects.components.autolayout;

import java.util.List;

import net.merayen.elastic.ui.UIObject;

public class LayoutMethods { // XXX Factory methods are perhaps not that good in this case, especially if parameters are changing very quickly (GC stress)
	public static AutoLayout.Placement horizontal(float margin) {
		return (List<UIObject> objects) -> {
			float x = margin;
			for(UIObject obj : objects) {
				obj.translation.x = x;
				obj.translation.y = 0;

				x += obj.getWidth() + margin;
			}
		};
	}

	public static AutoLayout.Placement vertical(float margin) {
		return (List<UIObject> objects) -> {
			float y = margin;
			for(UIObject obj : objects) {
				obj.translation.x = 0;
				obj.translation.y = y;

				y += obj.getHeight() + margin;
			}
		};
	}

	public static AutoLayout.Placement horizontalBox(float margin, float max_width) {
		return (List<UIObject> objects) -> {
			float x = margin;
			float y = margin;
			float height = 0;

			for(UIObject obj : objects) {
				float obj_width = obj.getWidth();
				float obj_height = obj.getHeight();

				if(x + obj_width + margin > max_width) {
					x = margin;
					y += height + margin;
					height = 0;
				}

				obj.translation.x = x;
				obj.translation.y = y;
				
				x += obj_width + margin;

				height = Math.max(height, obj_height);
			}
		};
	}
}
