package net.merayen.elastic.ui.objects.top.viewport;

import net.merayen.elastic.ui.Draw;
import org.json.simple.JSONObject;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.util.UINodeUtil;

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
	float ratio; // Value from 0 to 1, telling how much of the layoutWidth or layoutHeight this viewport takes from the view
	public View view; // The view to draw. Set this and we will change to it on next onUpdate()

	//private final ViewportContainer viewport_container;
	private View current_view; // Actual view
	private UIClip clip = new UIClip();
	private ViewportDrag drag;
	private Handler handler;

	private static final int BORDER_WIDTH = 2;
	private static int lol_c;
	private int lol = lol_c++;

	private float original_size;

	Viewport(Handler handler) {
		this.handler = handler;
		clip.getTranslation().x = BORDER_WIDTH;
		clip.getTranslation().y = BORDER_WIDTH;
		add(clip);
	}

	@Override
	public void onInit() {
		Viewport self = this;

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
				UINodeUtil.getWindow(self).getDebug().set("ViewportDrag onDrag()", diff + "," + vertical + ", " + original_size);
				float relative_diff = original_size + diff;
				handler.onNewViewportResize(relative_diff, vertical);
			}

			@Override
			public void onDrop(float diff, boolean vertical) {
				UINodeUtil.getWindow(self).getDebug().set("ViewportDrag onDrop()", diff + ", " + vertical);
			}
		});
		clip.add(drag);
	}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(150, 150, 150);
		draw.setStroke(BORDER_WIDTH);
		draw.rect(BORDER_WIDTH, BORDER_WIDTH, width - BORDER_WIDTH * 2, height - BORDER_WIDTH * 2);

		draw.setColor(200, 200, 200);
		draw.setStroke(BORDER_WIDTH / 2f);
		draw.rect(BORDER_WIDTH, BORDER_WIDTH, width - BORDER_WIDTH * 2, height - BORDER_WIDTH * 2);
	}

	@Override
	public void onUpdate() {
		if(current_view != view) {
			if(current_view != null)
				clip.remove(current_view);

			if(view != null) {
				clip.add(view, 0);
				current_view = view;
			}
		}

		clip.setLayoutWidth(width - BORDER_WIDTH * 2);
		clip.setLayoutHeight(height - BORDER_WIDTH * 2);

		if(current_view != null) {
			current_view.setLayoutWidth(width - BORDER_WIDTH * 2);
			current_view.setLayoutHeight(height - BORDER_WIDTH * 2);
		}

		drag.width = width - BORDER_WIDTH * 2;
		drag.height = height - BORDER_WIDTH * 2;
	}

	public String toString() {
		return "Viewport(" + lol + ")";
	}

	public ViewportContainer getViewportContainer() {
		return (ViewportContainer) getParent();
	}
}
