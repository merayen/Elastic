package net.merayen.elastic.ui.objects.top.viewport;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.KeyboardEvent;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.intercom.ViewportHelloMessage;
import net.merayen.elastic.ui.objects.top.easymotion.Branch;
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.objects.top.views.splashview.SplashView;
import net.merayen.elastic.ui.util.HitTester;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.MutablePoint;
import net.merayen.elastic.util.TaskQueue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import java.util.*;

/**
 * Contains all the viewports.
 */
public class ViewportContainer extends UIObject implements EasyMotionBranch, TaskQueue.RunsTasks {
	public float width, height;
	List<Viewport> viewports = new ArrayList<>(); // Flat list of all the viewports
	private Layout layout;
	private Viewport dragging_viewport;
	private TaskQueue taskQueue = new TaskQueue();
	private MouseHandler mouse_handler;
	private long blink;

	public void addViewport(View view) {
		if (view.getParent() != null)
			throw new RuntimeException("View is already in use");

		Viewport viewport = createViewport(view);
		layout.splitHorizontal(viewports.get(0), viewport);
		layout.resizeHeight(viewports.get(0), 0.2f); // Lol, no. Wrong.
	}

	/**
	 * Swaps a view with another one.
	 */
	public void swapView(View old, View view) {
		Viewport viewport = viewports.stream().filter((x) -> x.getView() == old).findFirst().get();

		if (viewport == null)
			throw new RuntimeException("Old view does not exist");

		viewport.setView(view);
	}

	public List<Viewport> getViewports() {
		return Collections.unmodifiableList(viewports);
	}

	private void defaultView() { // Testing purposes probably
		Viewport a = createViewport(new SplashView());
		layout = new Layout(a);

		sendMessage(new ViewportHelloMessage(this));
	}

	private boolean me;

	@Override
	public void onInit() {
		ViewportContainer self = this;

		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			private Viewport moving;
			private boolean vertical;

			@Override
			public void onMouseDown(MutablePoint position) {
				for (Viewport v : viewports)
					if (HitTester.inside(position, new Rect(
							v.getTranslation().x + v.width - 4,
							v.getTranslation().y,
							v.getTranslation().x + v.width + 4,
							v.getTranslation().y + v.height)
					)) {
						moving = v;
						vertical = true;
					} else if (HitTester.inside(position, new Rect(
							v.getTranslation().x,
							v.getTranslation().y + v.height - 4,
							v.getTranslation().x + v.width,
							v.getTranslation().y + v.height + 4)
					)) {
						moving = v;
						vertical = false;
					}

				me = true;
			}

			@Override
			public void onMouseDrag(MutablePoint position, MutablePoint offset) {
				if (moving == null)
					return;

				if (vertical) {
					layout.resizeWidth(moving, (position.getX() - moving.getTranslation().x) / width);
				} else {
					layout.resizeHeight(moving, (position.getY() - moving.getTranslation().y) / height);
				}
			}

			@Override
			public void onGlobalMouseUp(MutablePoint position) {
				me = false;
				moving = null;

				self.getTaskQueue().add(() -> { clean(); return true; });
			}

			/**
			 * Looks for too small/hidden viewports. They get removed.
			 * Happens when user minimizes a Viewport and let go off the mouse --> remove Viewport.
			 */
			private void clean() {
				for (int i = viewports.size() - 1; i > -1; i--) {
					Viewport v = viewports.get(i);

					if (v.width <= 10 || v.height <= 10) {
						self.remove(v);
						viewports.remove(v);
						layout.remove(v);
					}
				}
			}
		});
		defaultView();
	}

	@Override
	public void onDraw(Draw draw) {
		if (me)
			draw.setColor(255, 0, 255);
		else
			draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);

		if (blink > System.currentTimeMillis()) {
			draw.setColor(1f, 0.0f, 0.0f);
			draw.fillRect(0, 0, width, height);
		}
	}

	@Override
	public void onUpdate() {
		updateLayout();
		taskQueue.update();
	}

	@Override
	public void onEvent(UIEvent event) {
		mouse_handler.handle(event);
	}

	private void updateLayout() {
		for (Layout.CalculatedPosition p : layout.getLayout()) {
			Viewport v = ((Viewport) p.obj);
			v.getTranslation().x = p.x * width;
			v.getTranslation().y = p.y * height;
			v.width = p.width * width;
			v.height = p.height * height;
		}
	}

	private Viewport createViewport(View view) {
		class omgJava {
			Viewport newViewport;
		}

		omgJava omgJava = new omgJava();
		System.out.println("createViewport called");

		ViewportContainer self = this;

		Viewport v = new Viewport(new Viewport.Handler() {
			@Override
			public void onNewViewport(boolean vertical) { // TODO refuse creation if we are too small
				Viewport v = createViewport(omgJava.newViewport.getView().cloneView());
				if(vertical) {
					layout.splitVertical(omgJava.newViewport, v);
				} else { // Horizontal
					layout.splitHorizontal(omgJava.newViewport, v);
				}

				dragging_viewport = omgJava.newViewport;
			}

			@Override
			public void onNewViewportResize(float new_size, boolean vertical) {
				if (dragging_viewport == null)
					throw new RuntimeException("Should not happen");

				if (width == 0 || height == 0)
					return;

				// TODO refuse smaller than a certain value
				if (vertical)
					layout.resizeWidth(omgJava.newViewport, new_size / width);
				else
					layout.resizeHeight(omgJava.newViewport, new_size / height);

				UINodeUtil.INSTANCE.getWindow(self).getDebug().set("ViewContainer new_size", new_size / (vertical ? width : height));
			}
		});

		v.setView(view);
		add(v);
		viewports.add(v);
		omgJava.newViewport = v; // Hack
		return v;
	}

	public void blinkRed() {
		blink = System.currentTimeMillis() + 100;
	}

	// EASYMOTION
	private Branch branch = new Branch(this, this) {
		{
			for (int i = 0; i < 10; i++) {
				int paneNumber = i;

				Control control = new Control((keys) -> {
					if (paneNumber < viewports.size()) {
						if (easyMotionMode.equals(EasyMotionMode.ENTER)) {
							return viewports.get(paneNumber).getView();
						} else if (easyMotionMode.equals(EasyMotionMode.CHANGE)) {
							System.out.println("ViewportContainer is supposed to show a dialog with alternative view to swap current view with");
						}
					}
					return null;
				});

				HashSet<KeyboardEvent.Keys> keys = new HashSet<>();
				keys.add(KeyboardEvent.Companion.getNumbers()[i]);

				getControls().put(keys, control);
			}
		}

		{
			Control control = new Control((keys) -> Control.Companion.getSTEP_BACK());

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

	public enum EasyMotionMode {
		ENTER,
		CHANGE
	}

	public EasyMotionMode easyMotionMode;

	@Override
	public TaskQueue getTaskQueue() {
		return taskQueue;
	}
}
