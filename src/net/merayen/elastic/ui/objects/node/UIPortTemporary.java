package net.merayen.elastic.ui.objects.node;

public class UIPortTemporary extends UIPort {
	/*
	 * This port is created when user starts to drag a line from a port.
	 * Is invisible.
	 * Only to be used by UIPort internally!
	 */
	private UIPort source_port;

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
}
