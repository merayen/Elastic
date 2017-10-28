package net.merayen.elastic.ui.objects.top.views.arrangementview;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Button;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;

class TrackHeader extends UIObject {
	interface Handler {
		public void onRemove();
	}

	float width, height;

	private final AutoLayout buttons = new AutoLayout(new LayoutMethods.HorizontalBox(10));
	private Handler handler;

	@Override
	protected void onInit() {
		buttons.add(new Button(){{
			label = "X";
			if(handler != null)
				handler.onRemove();
		}});

		add(buttons);
	}

	@Override
	protected void onDraw() {
		draw.setColor(50, 0, 50);
		draw.fillRect(2, 2, width - 4, height - 4);
	}

	void setHandler(Handler handler) {
		this.handler = handler;
	}
}
