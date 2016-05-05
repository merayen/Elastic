package net.merayen.elastic.ui.objects.top.viewport;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.top.views.View;

/**
 * A viewport. Yes. Yup, it is.
 * We can have multiple viewports for a single window.
 * 
 * Holds one object that it only sets as a child when drawing, and removes it afterwards.
 */
public class Viewport extends UIObject {
	public float width, height;
	public View view; // The view to draw. Set this and we will change to it on next onUpdate()

	private final ViewportContainer viewport_container;
	private View current_view; // Actual view
	private UIClip clip = new UIClip();

	public Viewport(ViewportContainer vc) {
		this.viewport_container = vc;
	}

	@Override
	protected void onInit() {
		clip.translation.x = 10;
		clip.translation.y = 10;
		add(clip);
	}

	/*@Override
	protected void onDraw() {
		super.onDraw();

		draw.setColor(20, 20, 50);
		draw.fillRect(5, 5, width, height - 5);
	}*/

	@Override
	protected void onUpdate() {
		if(current_view != view) {
			if(current_view != null)
				clip.remove(current_view);

			if(view != null) {
				clip.add(view);
				current_view = view;
			}
		}

		clip.width = width - 20;
		clip.height = height - 20;

		if(current_view != null) {
			current_view.width = width - 20;
			current_view.height = height - 20;
		}
	}
}
