package net.merayen.elastic.ui.objects.top;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.controller.Gate.UIGate;
import net.merayen.elastic.util.Postmaster;

/**
 * The very topmost object.
 * Holds track of all the windows, and which to draw in which context.
 */
public class Top extends UIObject {
	private final List<Window> windows = new ArrayList<>();
	UIGate ui_gate;

	@Override
	protected void onInit() {
		// Create a default window
		Window w = new Window(this);
		windows.add(w);
		add(w);
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

	public void sendMessage(Postmaster.Message message) {
		ui_gate.send(message);
	}
}
