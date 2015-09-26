package net.merayen.merasynth.ui.objects;

import net.merayen.merasynth.ui.Rect;
import net.merayen.merasynth.ui.event.IEvent;

/*
 * Draws children inside ourself, clipping by our size.
 */
public class UIClip extends UIGroup {
	public float width = 10f;
	public float height = 10f;

	@Override
	protected void onDraw() {
		translation.clip = new Rect(0, 0, width, height);
		draw.setColor(255,0,255);
		draw.setStroke(0.5f);
		draw.rect(0.1f, 0.1f, width - 0.2f, height - 0.2f);
		super.onDraw();
	}
}
