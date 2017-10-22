package net.merayen.elastic.ui.objects.components.curvebox;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;

public class SignalBezierCurveBoxControlFrame extends UIObject {
	public float width = 100;
	public float height = 100;

	public final SignalBezierCurveBox bezier = new SignalBezierCurveBox();
	private final AutoLayout buttons = new AutoLayout(LayoutMethods.horizontal(2));

	@Override
	protected void onInit() {
		bezier.translation.y = 20;
		add(bezier);
	}

	@Override
	protected void onUpdate() {
		bezier.width = width;
		bezier.height = Math.max(0, height - 20);
	}
}
