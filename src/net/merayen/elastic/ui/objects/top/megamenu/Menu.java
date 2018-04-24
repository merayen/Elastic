package net.merayen.elastic.ui.objects.top.megamenu;

import net.merayen.elastic.ui.Draw;
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
	public void onInit() {
		Menu self = this;

		super.onInit();
		views = new ViewSelector((cls) -> self.getSearch().parentByType(View.class).swap(cls));
		add(views);

		add(new Button() {{
			label = "New Project";
			auto_dimension = false;
			width = 196;
			setHandler(() -> {});
		}});

		add(new Button() {{
			label = "Open Project";
			setHandler(() -> self.getSearch().parentByType(View.class).swap(FileBrowserView.class));
		}});

		add(new Button() {{
			label = "About";
			setHandler(() -> {});
		}});
	}

	@Override
	public void onDraw(Draw draw) {
		super.onDraw(draw);

		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, 200, 400);
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.rect(0, 0, 200, 400);
	}
}
