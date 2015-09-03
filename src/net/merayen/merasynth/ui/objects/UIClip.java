package net.merayen.merasynth.ui.objects;

/*
 * Draws children inside ourself, clipping by our size.
 */
public class UIClip extends UIGroup {
	public float width = 10f;
	public float height = 10f;

	@Override
	protected void onDraw() {
		draw.clip(0, 0, width, height);
		super.onDraw();
	}

	@Override
	protected void onChildrenDrawn() {
		draw.popClip();
	}
}
