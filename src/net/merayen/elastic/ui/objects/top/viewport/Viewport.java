package net.merayen.elastic.ui.objects.top.viewport;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.objects.top.views.View;

/**
 * A viewport. Yes. Yup, it is.
 * We can have multiple viewports for a single window.
 * 
 * Holds one object that it only sets as a child when drawing, and removes it afterwards.
 */
public class Viewport extends UIObject {
	interface Handler {
		public void onNewViewport(boolean vertical);
		public void onNewViewportResize(float width, boolean is_width); // Resizing of the left/over Viewport to increase size of the newly created Viewport. Negative value. One of the parameters is always 0 
	}

	float width, height;
	float ratio; // Value from 0 to 1, telling how much of the width or height this viewport takes from the view
	public View view; // The view to draw. Set this and we will change to it on next onUpdate()

	//private final ViewportContainer viewport_container;
	private View current_view; // Actual view
	private UIClip clip = new UIClip();
	private ViewportDrag drag;
	private Handler handler;

	private static int lol_c;
	private int lol = lol_c++;

	private float original_size;

	public Viewport(Handler handler) {
		this.handler = handler;
		clip.translation.x = 10;
		clip.translation.y = 10;
		add(clip);
	}

	@Override
	protected void onInit() {
		drag = new ViewportDrag(new ViewportDrag.Handler(){
			@Override
			public void onStartDrag(float diff, boolean vertical) {
				if(vertical) {
					handler.onNewViewport(true);
					original_size = width;
				} else {
					handler.onNewViewport(false);
					original_size = height;
				}
			}

			@Override
			public void onDrag(float diff, boolean vertical) {
				((Top)search.getTop()).debugPrint("ViewportDrag onDrag()", diff + "," + vertical + ", " + original_size);
				float relative_diff = original_size + diff;
				handler.onNewViewportResize(relative_diff, vertical);
			}

			@Override
			public void onDrop(float diff, boolean vertical) {
				((Top)search.getTop()).debugPrint("ViewportDrag onDrop()", diff + ", " + vertical);
			}
		});
		clip.add(drag);
	}

	@Override
	protected void onUpdate() {
		if(current_view != view) {
			if(current_view != null)
				clip.remove(current_view);

			if(view != null) {
				clip.add(view, true);
				current_view = view;
			}
		}

		clip.width = width - 20;
		clip.height = height - 20;

		if(current_view != null) {
			current_view.width = width - 20;
			current_view.height = height - 20;
		}

		drag.width = width - 20;
		drag.height = height - 20;

		((Top)search.getTop()).debugPrint("Viewport " + lol, translation + "   [" + width + ", " + height + "]");
	}

	JSONObject dump() {
		return null;
	}

	public String toString() {
		return "Viewport(" + lol + ")";
	}
}
