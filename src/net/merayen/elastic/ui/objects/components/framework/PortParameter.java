package net.merayen.elastic.ui.objects.components.framework;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

/**
 * Defines a port, and shows one view if that port is not connected, and shows another one if it is.
 * Doesn't show any UI itself, but add it to your UIObject to make it work.
 */
public class PortParameter extends UIObject {
	private final UINode node;
	private final UIPort port;
	public final UIObject not_connected;
	public final UIObject connected;

	private long last_port_check;

	public PortParameter(UINode node, UIPort port, UIObject not_connected, UIObject connected) {
		this.node = node;
		this.port = port;
		this.not_connected = not_connected;
		this.connected = connected;
	}

	@Override
	protected void onUpdate() {
		if(last_port_check + 100 < System.currentTimeMillis()) {
			last_port_check = System.currentTimeMillis();
			if(node.getUINet().isConnected(port)) {
				if(connected.getParent() == null)
					add(connected);
				if(not_connected.getParent() != null)
					remove(not_connected);
			} else {
				if(connected.getParent() != null)
					remove(connected);
				if(not_connected.getParent() == null)
					add(not_connected);
			}
		}
	}
}
