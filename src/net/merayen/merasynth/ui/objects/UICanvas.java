package net.merayen.merasynth.ui.objects;

/*
 * Draws children inside ourself, clipping by our size.
 * Runs our own UIGroup with our own surface, which we then paint on the current surface.
 */
public class UICanvas extends UIObject {
	private static class Content extends UIGroup {

	}

	public float width = 10f;
	public float height = 10f;

	private Content content;
	//private DrawContext d;

	@Override
	protected void onInit() {
		content = new Content();
		this.getPixelDimension(width, height);
		this.draw.g2d.create();
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		//content.updateDraw();
	}

	public Content getContentPane() {
		return content;
	}
}
