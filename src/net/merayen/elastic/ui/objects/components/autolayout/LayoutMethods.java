package net.merayen.elastic.ui.objects.components.autolayout;

import java.util.List;

import net.merayen.elastic.ui.UIObject;

public class LayoutMethods {
	private LayoutMethods() {}

	public static class HorizontalBox implements AutoLayout.Placement {
		public float margin;
		public float max_width = 100;
		private float width, height;

		public HorizontalBox() {}

		public HorizontalBox(float margin, float max_width) {
			this.margin = margin;
			this.max_width = max_width;
		}

		public HorizontalBox(float margin) {
			this.margin = margin;
		}

		@Override
		public void place(List<UIObject> objects) {
			float x = margin;
			float y = margin;
			float row_height = 0;
			height = 0;
			width = 0;

			for(UIObject obj : objects) {
				float obj_width = obj.getWidth();
				float obj_height = obj.getHeight();

				if(x + obj_width + margin > max_width) {
					x = margin;
					y += row_height + margin;
					row_height = 0;
				}

				obj.getTranslation().x = x;
				obj.getTranslation().y = y;

				x += obj_width + margin;

				row_height = Math.max(row_height, obj_height);
				height = Math.max(height, y + obj_height);
				width = Math.max(width, x + obj_width);
			}
		}

		@Override
		public float getWidth() {
			return width;
		}

		@Override
		public float getHeight() {
			return height;
		};
	}
}
