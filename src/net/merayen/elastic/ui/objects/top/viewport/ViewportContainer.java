package net.merayen.elastic.ui.objects.top.viewport;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.intercom.ViewportHelloMessage;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;

/**
 * Contains all the viewports.
 * TODO implement layouting
 */
public class ViewportContainer extends UIObject {
	public float width, height;
	List<Viewport> viewports = new ArrayList<>(); // Flat list of all the viewports

	public void addViewport(Viewport viewport) {
		viewports.add(viewport);
	}

	public List<Viewport> getViewports() {
		return new ArrayList<>(viewports);
	}

	private void defaultView() { // Testing purposes probably
		Viewport m;

		m = new Viewport(this);
		m.view = new NodeView();
		add(m);
		viewports.add(m);

		m = new Viewport(this);
		m.view = new NodeView();
		add(m);
		viewports.add(m);
		
		m = new Viewport(this);
		m.view = new NodeView();
		add(m);
		viewports.add(m);

		sendMessage(new ViewportHelloMessage(this));
	}

	@Override
	protected void onInit() {
		defaultView();
	}

	/*@Override
	protected void onDraw() {
		((Top)search.getTop()).debug.set("ViewContainer.Absolute", this.absolute_translation);
		((Top)search.getTop()).debug.set("ViewContainer.OutlineAbsolute", this.outline_abs_px);
	}*/

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
}
