package net.merayen.elastic.ui.objects.top.viewbar;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.ui.objects.top.views.View;

public class ViewBar extends UIObject {
	public float width;
	private final float height = 20;
	private final UIObject general;
	protected final UIObject content = new UIObject();

	public ViewBar() {
		ViewBar self = this;
		general = new ViewSelector(new ViewSelector.Handler() {
			@Override
			public void onSelect(Class<? extends View> cls) {
				View old_view = self.search.parentByType(View.class);
				View new_view;

				try {
					new_view = cls.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				self.search.parentByType(ViewportContainer.class).swapView(old_view, new_view);
			}
		});
	}

	@Override
	protected void onInit() {
		add(general);
		add(content);
	}

	@Override
	protected void onDraw() {
		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.line(0, height, width, height);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if(general.isInitialized())
			content.translation.x = general.getDeepOutline().getWidth() + 5;
	}
}
