package net.merayen.elastic.ui.objects.top.viewport;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.intercom.ViewportContainerUpdateMessage;
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

	public void defaultView() { // Testing purposes probably
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

		sendMessage(new ViewportContainerUpdateMessage(viewports));
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
}
