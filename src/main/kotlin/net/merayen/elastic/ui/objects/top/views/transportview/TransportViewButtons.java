package net.merayen.elastic.ui.objects.top.views.transportview;

import net.merayen.elastic.ui.objects.components.buttons.Button;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;

public class TransportViewButtons extends AutoLayout {
	public TransportViewButtons() {
		super(new LayoutMethods.HorizontalBox(2, 10000));
	}

	@Override
	public void onInit() {
		super.onInit();

		add(new Button() {{
			setLabel("|<-");
			setFontSize(20);
		}});

		add(new Button() {{
			setLabel("<-");
			setFontSize(20);
		}});

		add(new Button() {{
			setLabel("I>");
			setFontSize(20);
		}});

		add(new Button() {{
			setLabel("->");
			setFontSize(20);
		}});
	}
}
