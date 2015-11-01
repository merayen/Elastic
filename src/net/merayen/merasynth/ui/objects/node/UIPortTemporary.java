package net.merayen.merasynth.ui.objects.node;

/*
 * This port is created when user starts to drag a line from a port.
 * Is invisible.
 * Only to be used by UIPort internally!
 */
public class UIPortTemporary extends UIPort {
	public UIPortTemporary() {
		super("temporary_dragging_port", false);
		draw_default_port = false;
	}

	public void addTempPort(UIPort source_port) { // Must be called after you have added this class via add(...)
		getUINetObject().setDraggingPort(source_port, this);
	}

	public void removeTempPort() {
		getUINetObject().removeDraggingPort();
	}
}
