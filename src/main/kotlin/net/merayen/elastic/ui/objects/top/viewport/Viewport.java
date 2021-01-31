package net.merayen.elastic.ui.objects.top.viewport;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.KeyboardEvent;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.top.easymotion.Branch;
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.util.UINodeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * A viewport. Yes. Yup, it is.
 * We can have multiple viewports for a single window.
 * <p>
 * Holds one object that it only sets as a child when drawing, and removes it afterwards.
 */
public class Viewport extends UIObject implements EasyMotionBranch {
	interface Handler {
		void onNewViewport(boolean vertical);

		void onNewViewportResize(float width, boolean is_width); // Resizing of the left/over Viewport to increase size of the newly created Viewport. Negative value. One of the parameters is always 0
	}

	float width, height;
	private View view;

	private UIClip clip = new UIClip();
	private ViewportDrag drag;
	private Handler handler;

	private static final int BORDER_THICKNESS = 5;
	private static int lol_c;
	private int lol = lol_c++;

	private boolean selected = false;

	private float original_size;

	Viewport(Handler handler) {
		this.handler = handler;
		clip.getTranslation().x = BORDER_THICKNESS;
		clip.getTranslation().y = BORDER_THICKNESS;
		add(clip);

		Viewport self = this;

		drag = new ViewportDrag(new ViewportDrag.Handler() {
			@Override
			public void onStartDrag(float diff, boolean vertical) {
				if (vertical) {
					handler.onNewViewport(true);
					original_size = width;
				} else {
					handler.onNewViewport(false);
					original_size = height;
				}
			}

			@Override
			public void onDrag(float diff, boolean vertical) {
				UINodeUtil.INSTANCE.getWindow(self).getDebug().set("ViewportDrag onDrag()", diff + "," + vertical + ", " + original_size);
				float relative_diff = original_size + diff;
				handler.onNewViewportResize(relative_diff, vertical);
			}

			@Override
			public void onDrop(float diff, boolean vertical) {
				UINodeUtil.INSTANCE.getWindow(self).getDebug().set("ViewportDrag onDrop()", diff + ", " + vertical);
			}
		});
		clip.add(drag);
	}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(150, 150, 150);
		draw.setStroke(BORDER_THICKNESS);
		draw.rect(BORDER_THICKNESS, BORDER_THICKNESS, width - BORDER_THICKNESS * 2, height - BORDER_THICKNESS * 2);

		if (selected)
			draw.setColor(200, 200, 200);
		else
			draw.setColor(255, 255, 255);

		draw.setStroke(BORDER_THICKNESS / 2f);
		draw.rect(BORDER_THICKNESS, BORDER_THICKNESS, width - BORDER_THICKNESS * 2, height - BORDER_THICKNESS * 2);
	}

	@Override
	public void onUpdate() {
		clip.setLayoutWidth(width - BORDER_THICKNESS * 2);
		clip.setLayoutHeight(height - BORDER_THICKNESS * 2);

		if(view != null) {
			view.setLayoutWidth(width - BORDER_THICKNESS * 2);
			view.setLayoutHeight(height - BORDER_THICKNESS * 2);
		}

		drag.width = width - BORDER_THICKNESS * 2;
		drag.height = height - BORDER_THICKNESS * 2;
	}

	public void setView(View newView) {
		view = newView;

		clip.removeAll();
		clip.add(newView);
		clip.add(drag);
	}

	public View getView() {
		return view;
	}

	public String toString() {
		return "Viewport(" + lol + ")";
	}

	public ViewportContainer getViewportContainer() {
		return (ViewportContainer) getParent();
	}

	private Branch branch = new Branch(this, this) {
		{
			setHandler(new Branch.Handler() {
				@Override
				public void onEnter() {
					selected = true;
				}

				@Override
				public void onLeave() {
					selected = false;
				}
			});
		}
		{
			Control control = new Control((keyStroke -> Control.Companion.getSTEP_BACK()));

			HashSet<KeyboardEvent.Keys> keys = new HashSet<>();
			keys.add(KeyboardEvent.Keys.Q);

			getControls().put(keys, control);
		}
	};

	@NotNull
	@Override
	public Branch getEasyMotionBranch() {
		return branch;
	}
}
