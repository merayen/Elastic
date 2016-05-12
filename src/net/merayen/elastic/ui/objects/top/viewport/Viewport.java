package net.merayen.elastic.ui.objects.top.viewport;

import org.json.simple.JSONObject;

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
	float width, height;
	float ratio; // Value from 0 to 1, telling how much of the width or height this viewport takes from the view
	public View view; // The view to draw. Set this and we will change to it on next onUpdate()

	private final ViewportContainer viewport_container;
	private View current_view; // Actual view
	private UIClip clip = new UIClip();

	public Viewport(ViewportContainer vc) {
		this.viewport_container = vc;
		clip.translation.x = 10;
		clip.translation.y = 10;
		add(clip);
	}

	@Override
	protected void onDraw() {
		draw.setColor(100, 100, 100); // Move out to separate UIObject, make interactable
		draw.setStroke(2);
		for(int i = 5; i < 8; i++)
			draw.line(width - i * 5, 0, width, 5 * i);
	}

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

	JSONObject dump() {
		return null;
	}
}
