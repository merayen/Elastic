package net.merayen.elastic.ui.objects.top.viewport;

import java.util.*;

import net.merayen.elastic.ui.Draw;

import net.merayen.elastic.ui.Rect;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.intercom.ViewportHelloMessage;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.objects.top.views.splashview.SplashView;
import net.merayen.elastic.ui.util.HitTester;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.TaskExecutor;

/**
 * Contains all the viewports.
 */
public class ViewportContainer extends UIObject {
	public float width, height;
	List<Viewport> viewports = new ArrayList<>(); // Flat list of all the viewports
	private Layout layout;
	private Viewport dragging_viewport;
	private TaskExecutor task_executor = new TaskExecutor();
	private MouseHandler mouse_handler;

	public void addViewport(View view) {
		if(view.getParent() != null)
			throw new RuntimeException("View is already in use");

		Viewport viewport = createViewport(view);
		layout.splitHorizontal(viewports.get(0), viewport);
		layout.resizeHeight(viewports.get(0), 0.2f); // Lol, no. Wrong.
	}

	/**
	 * Add a task in the domain of ViewportContainer.
	 */
	public void addTask(TaskExecutor.Task task) {
		task_executor.add(task);
	}

	/**
	 * Swaps a view with another one.
	 */
	public void swapView(View old, View view) {
		Viewport viewport = viewports.stream().filter((x) -> x.view == old).findFirst().get();

		if(viewport == null)
			throw new RuntimeException("Old view does not exist");

		viewport.view = view;
	}

	public List<Viewport> getViewports() {
		return Collections.unmodifiableList(viewports);
	}

	private void defaultView() { // Testing purposes probably
		Viewport a = createViewport(new SplashView());
		layout = new Layout(a);

		sendMessage(new ViewportHelloMessage(this));
	}

	boolean me;
	@Override
	public void onInit() {
		ViewportContainer self = this;

		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			private Viewport moving;
			private boolean vertical;

			@Override
			public void onMouseDown(Point position) {
				for(Viewport v : viewports)
					if(HitTester.inside(position, new Rect(
						v.getTranslation().x + v.width - 4,
						v.getTranslation().y,
						v.getTranslation().x + v.width + 4,
						v.getTranslation().y + v.height)
					)) {
						moving = v;
						vertical = true;
					}

					else if(HitTester.inside(position, new Rect(
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
			public void onMouseDrag(Point position, Point offset) {
				if(moving == null)
					return;

				if(vertical) {
					layout.resizeWidth(moving, (position.x - moving.getTranslation().x) / width);
				} else {
					layout.resizeHeight(moving, (position.y - moving.getTranslation().y) / height);
				}
			}

			@Override
			public void onGlobalMouseUp(Point position) {
				me = false;
				moving = null;

				self.addTask(new TaskExecutor.Task(new Object(), 0, () -> clean()));
			}

			/**
			 * Looks for too small/hidden viewports. They get removed.
			 * Happens when user minimizes a Viewport and let go off the mouse --> remove Viewport.
			 */
			private void clean() {
				for(int i = viewports.size() - 1; i > -1; i--) {
					Viewport v = viewports.get(i);

					if(v.width <= 10 || v.height <= 10) {
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
		if(me)
			draw.setColor(255,  0,  255);
		else
			draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
	}

	@Override
	public void onUpdate() {
		// TODO Remove. Requires defaultView to have been called
		int i = 0;
		for(Viewport v : viewports) {
			v.getTranslation().x = (width / viewports.size()) * i;
			v.width = width - (width / viewports.size()) * i;

			v.getTranslation().y = (height / viewports.size()) * i;
			v.height = height / viewports.size();
			i++;
		}

		updateLayout();

		task_executor.update();
	}

	@Override
	public void onEvent(UIEvent event) {
		mouse_handler.handle(event);
	}

	private void updateLayout() {
		for(Layout.CalculatedPosition p : layout.getLayout()) {
			Viewport v = ((Viewport)p.obj);
			v.getTranslation().x = p.x * width;
			v.getTranslation().y = p.y * height;
			v.width = p.width * width;
			v.height = p.height * height;
		}
	}

	private Viewport createViewport(View view) {
		final Map<String, Object> m = new HashMap<>();

		ViewportContainer self = this;

		Viewport v = new Viewport(new Viewport.Handler() {
			@Override
			public void onNewViewport(boolean vertical) { // TODO refuse creation if we are too small
				Viewport v = createViewport(view.cloneView());
				if(vertical) {
					layout.splitVertical(m.get("viewport"), v);
				} else { // Horizontal
					layout.splitHorizontal(m.get("viewport"), v);
				}
				dragging_viewport = (Viewport)m.get("viewport");
			}

			@Override
			public void onNewViewportResize(float new_size, boolean vertical) {
				if(dragging_viewport == null)
					throw new RuntimeException("Should not happen");

				if(width == 0 || height == 0)
					return;

				// TODO refuse smaller than a certain value 
				if(vertical)
					layout.resizeWidth(m.get("viewport"), new_size / width);
				else
					layout.resizeHeight(m.get("viewport"), new_size / height);

				UINodeUtil.INSTANCE.getWindow(self).getDebug().set("ViewContainer new_size", new_size / (vertical ? width : height));
			}
		});

		v.view = view;
		add(v);
		viewports.add(v);
		m.put("viewport", v);
		return v;
	}
}
