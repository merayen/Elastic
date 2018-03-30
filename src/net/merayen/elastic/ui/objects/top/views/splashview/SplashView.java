package net.merayen.elastic.ui.objects.top.views.splashview;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.objects.top.views.View;

public class SplashView extends View {
	private final SplashViewBar bar = new SplashViewBar();

	@Override
	public void onInit() {
		super.onInit();
		add(bar);
	}

	@Override
	public View cloneView() {
		return new SplashView();
	}

	@Override
	public void onDraw(Draw draw) {
		super.onDraw(draw);
		draw.setColor(255, 255, 100);
		draw.setFont("", 100);
		draw.text("Elastic", 10, 150);

		draw.fillOval(450, (float)(Math.sin(System.currentTimeMillis() / 1000.0) * 400) + 400, 100, 100);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		bar.width = width;
	}
}
