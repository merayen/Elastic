package net.merayen.elastic.ui.objects;

import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.Top;

/*
 * Draws children inside ourself, clipping by our size.
 */
public class UIClip extends UIObject {
	private static int count_id;
	private int count;
	public float width = 10f;
	public float height = 10f;

	@Override
	protected void onInit() {
		count = count_id++;
	}

	@Override
	protected void onDraw() {
		translation.clip = new Rect(0, 0, width, height);
		draw.setColor(255,127,0);
		draw.setStroke(2f);
		draw.rect(0.1f, 0.1f, width - 0.2f, height - 0.2f);

		/*((Top)search.getTop()).debug.set(String.format("UIClip.Absolute %d", count), this.absolute_translation);
		((Top)search.getTop()).debug.set(String.format("UIClip.OutlineAbsolute %d", count), this.outline_abs_px);*/
	}
}
