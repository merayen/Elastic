package net.merayen.merasynth.ui.objects.node;

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
		getNetObject().addLine(source_port, this);
		getNetObject().setDraggingPort(source_port);
	}

	public void removeTempPort() {
		getNetObject().removeLine(source_port, this);
		getNetObject().setDraggingPort(null);
	}
}
