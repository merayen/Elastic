package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.util.UINodeUtil;

public class UIPortTemporary extends UIPort {
	/*
	 * This port is created when user starts to drag a line from a port.
	 * Is invisible.
	 * Only to be used by UIPort internally!
	 */
	private UIPort source_port;
	public UIPort target; // Set by the port which the port is hanging over

	public UIPortTemporary() {
		super("temporary_dragging_port", false);
		draw_default_port = false;
	}

	public void addTempPort(UIPort source_port) { // Must be called after you have added this class via add(...)
		this.source_port = source_port;
		getUINetObject().addLine(source_port, this);
		getUINetObject().setDraggingPort(source_port, this);
	}

	public void removeTempPort() {
		getUINetObject().removeLine(source_port, this);
		getUINetObject().setDraggingPort(null, null);
	}

	@Override
	public void onDraw() {
		UINodeUtil.getWindow(this).debug.set("UIPortTemporary " + this, String.format("source_port=%s, target=%s", source_port == null ? null : source_port.name, target == null ? null : target.name));
	}
}
