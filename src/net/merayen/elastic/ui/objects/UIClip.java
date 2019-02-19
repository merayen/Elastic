package net.merayen.elastic.ui.objects;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;

/**
 * Draws children inside ourself, clipping by our size.
 * TODO implement caching?
 */
public class UIClip extends UIObject {
	public float width = 10f;
	public float height = 10f;

	@Override
	public void onDraw(Draw draw) {
		getTranslation().clip = new Rect(0, 0, width, height);
	}
}
