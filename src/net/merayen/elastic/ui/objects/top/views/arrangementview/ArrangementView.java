package net.merayen.elastic.ui.objects.top.views.arrangementview;

import net.merayen.elastic.ui.objects.top.views.View;

public class ArrangementView extends View {
	private final ArrangementViewBar bar = new ArrangementViewBar();
	private final Arrangement arrangement = new Arrangement();

	@Override
	protected void onInit() {
		super.onInit();

		add(bar);
		add(arrangement);
		arrangement.translation.y = 20;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		arrangement.width = width;
		arrangement.height = height - 20;
	}

	@Override
	public View cloneView() {
		return new ArrangementView();
	}
}
