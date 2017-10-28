package net.merayen.elastic.ui.objects.top.megamenu;

import net.merayen.elastic.ui.objects.components.Button;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;
import net.merayen.elastic.ui.objects.top.viewbar.ViewSelector;
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.objects.top.views.filebrowserview.FileBrowserView;

class Menu extends AutoLayout {
	private ViewSelector views;

	public Menu() {
		super(new LayoutMethods.HorizontalBox(2, 200));
	}

	@Override
	protected void onInit() {
		Menu self = this;

		super.onInit();
		views = new ViewSelector((cls) -> {
			View old_view = self.search.parentByType(View.class);
			View new_view;

			try {
				new_view = cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			self.search.parentByType(ViewportContainer.class).swapView(old_view, new_view);
		});
		add(views);

		add(new Button() {{
			label = "New Project";
			auto_dimension = false;
			width = 196;
			setHandler(() -> {});
		}});

		add(new Button() {{
			label = "Open Project";
			setHandler(() -> {
				View old_view = self.search.parentByType(View.class);
				View new_view;

				try {
					new_view = FileBrowserView.class.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				self.search.parentByType(ViewportContainer.class).swapView(old_view, new_view);
			});
		}});

		add(new Button() {{
			label = "About";
			setHandler(() -> {});
		}});
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, 200, 400);
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.rect(0, 0, 200, 400);
	}
}
