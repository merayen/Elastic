package net.merayen.merasynth.ui.objects;

import net.merayen.merasynth.ui.Rect;

/*
 * Draws children inside ourself, clipping by our size.
 */
public class UIClip extends UIGroup {
	public float width = 10f;
	public float height = 10f;

	@Override
	protected void onDraw() {
		//translation.clip = new Rect(0, 0, width, height);
		super.onDraw();
	}

	/*@Override
	protected void onChildrenDrawn() {
		draw.popClip();
	}*/
}
