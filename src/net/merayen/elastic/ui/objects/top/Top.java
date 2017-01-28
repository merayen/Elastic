package net.merayen.elastic.ui.objects.top;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.merayen.elastic.ui.SurfaceHandler;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.controller.Gate.UIGate;
import net.merayen.elastic.ui.surface.Surface;
import net.merayen.elastic.util.Postmaster;

/**
 * The very topmost object.
 * Holds track of all the windows (called surfaces), and which to draw in which context.
 */
public class Top extends UIObject {
	private final List<Window> windows = new ArrayList<>();
	private final SurfaceHandler surfacehandler;
	UIGate ui_gate;

	public Top(SurfaceHandler surfacehandler) {
		this.surfacehandler = surfacehandler;

		// Create a default window
		Window w = new Window(createSurface("default"));
		windows.add(w);
		add(w);
	}

	@Override
	protected void onInit() {
		
	}

	/**
	 * We override this method to return the correct UIObject for the window being drawn.
	 * TODO decide which Window()-object to return upon DrawContext-type.
	 */
	@SuppressWarnings("serial")
	@Override
	protected List<UIObject> onGetChildren() {
		return new ArrayList<UIObject>(){{
			add(windows.get(0));
		}}; // Supports only 1 window for now
	}

	public void setUIGate(UIGate ui_gate) {
		this.ui_gate = ui_gate;
	}

	public List<Window> getWindows() {
		return Collections.unmodifiableList(windows);
	}

	public Surface createSurface(String id) {
		return surfacehandler.createSurface(id);
	}

	public void sendMessage(Postmaster.Message message) {
		ui_gate.send(message);
	}
}
