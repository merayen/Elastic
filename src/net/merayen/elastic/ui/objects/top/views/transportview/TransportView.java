package net.merayen.elastic.ui.objects.top.views.transportview;

import net.merayen.elastic.ui.objects.top.views.View;

public class TransportView extends View {
	private TransportViewBar bar = new TransportViewBar();
	private TransportViewButtons buttons = new TransportViewButtons();

	@Override
	public void onInit() {
		super.onInit();
		add(bar);
		this.add(buttons);
		buttons.getTranslation().y = 20;
	}

	@Override
	public View cloneView() {
		return new TransportView();
	}

}
