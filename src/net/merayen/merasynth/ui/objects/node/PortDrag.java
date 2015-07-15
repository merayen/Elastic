package net.merayen.merasynth.ui.objects.node;

public class PortDrag extends UIPort {
	/*
	 * This port is created when user starts to drag a line from a port.
	 * Is invisible.
	 */
	public PortDrag() {
		super("temporary_dragging_port", false);
		draw_default_port = false;
	}
}
