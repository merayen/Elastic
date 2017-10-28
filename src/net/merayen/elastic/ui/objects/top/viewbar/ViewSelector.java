package net.merayen.elastic.ui.objects.top.viewbar;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Tabs;
import net.merayen.elastic.ui.objects.components.Tabs.Tab;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.ui.objects.top.views.splashview.SplashView;
import net.merayen.elastic.ui.objects.top.views.transportview.TransportView;

public class ViewSelector extends UIObject {
	public interface Handler {
		public void onSelect(Class<? extends View> cls);
	}

	private static class SelectorButton extends Tabs.TextButton {
		Class<? extends View> view;

		SelectorButton(String text, Class<? extends View> view) {
			setText(text, 10);
			this.view = view;
		}
	}

	private final Tabs tabs;
	private final Tabs.TextButton NODE_VIEW = new SelectorButton("Nodes", NodeView.class);
	private final Tabs.TextButton TRANSPORT_VIEW = new SelectorButton("Transport", TransportView.class);
	private final Tabs.TextButton ARRANGEMENT_VIEW = new SelectorButton("Arrangement", ArrangementView.class);
	private final Tabs.TextButton TEST_VIEW = new SelectorButton("Splash", SplashView.class);

	public ViewSelector(Handler handler) {
		tabs = new Tabs(new LayoutMethods.HorizontalBox(2, 100000), new Tabs.Handler() {
			@Override
			public void onSelect(Tab tab) {
				handler.onSelect(((SelectorButton)tab).view);
			}
		});
	}

	@Override
	protected void onInit() {
		tabs.translation.x = 2;
		tabs.translation.y = 2;
		add(tabs);

		tabs.add(NODE_VIEW);
		tabs.add(TRANSPORT_VIEW);
		tabs.add(ARRANGEMENT_VIEW);
		tabs.add(TEST_VIEW);
	}
}
