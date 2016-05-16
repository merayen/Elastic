package net.merayen.elastic.ui.objects.top.viewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.intercom.ViewportHelloMessage;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
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

	public void addViewport(Viewport viewport) {
		viewports.add(viewport);
	}

	/**
	 * Add a task in the domain of ViewportContainer.
	 */
	public void addTask(TaskExecutor.Task task) {
		task_executor.add(task);
	}

	public List<Viewport> getViewports() {
		return new ArrayList<>(viewports);
	}

	private void defaultView() { // Testing purposes probably
		Viewport a = createViewport(new NodeView());
		layout = new Layout(a);

		sendMessage(new ViewportHelloMessage(this));
	}

	@Override
	protected void onInit() {
		defaultView();
	}

	@Override
	protected void onDraw() {
		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
	}

	static int asdf;
	@Override
	protected void onUpdate() {
		// TODO Remove. Requires defaultView to have been called
		int i = 0;
		for(Viewport v : viewports) {
			v.translation.x = (width / viewports.size()) * i;
			v.width = width - (width / viewports.size()) * i;

			v.translation.y = (height / viewports.size()) * i; 
			v.height = height / viewports.size();
			i++;
		}

		updateLayout();
		if(asdf++ % 100 == 0) {
			System.out.println("ViewportContainer layout ");//
			for(Object o : layout.getLayout())
				System.out.println("\t" + o);
		}

		task_executor.update();
	}

	/**
	 * Called by the ViewportController when serializing.
	 */
	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();

		result.put("hi", "mom"); // TODO dump layout and type of view, and then dump all the views

		//for(Viewport v : viewports)
		//	v.dump();

		return result;
	}

	public void restore(JSONObject obj) {
		System.out.printf("ViewportContainer received restore: %s\n", obj);
	}

	private void updateLayout() {
		for(Layout.CalculatedPosition p : layout.getLayout()) {
			Viewport v = ((Viewport)p.obj);
			v.translation.x = p.x * width;
			v.translation.y = p.y * height;
			v.width = p.width * width;
			v.height = p.height * height;
		}
	}

	private Viewport createViewport(View view) {
		final Map<String, Object> m = new HashMap<>();

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

				((Top)search.getTop()).debug.set("ViewContainer new_size", new_size / (vertical ? width : height));
			}
		});

		v.view = view;
		add(v);
		viewports.add(v);
		m.put("viewport", v);
		return v;
	}
}
