package net.merayen.elastic.ui.objects.node;

import java.util.HashSet;

import net.merayen.elastic.ui.Color;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UINet;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class UIPort extends UIObject {
	public final static Color AUDIO_PORT = new Color(150, 200, 150);
	public final static Color MIDI_PORT = new Color(200, 150, 100);
	public final static Color AUX_PORT = new Color(150, 150, 150);

	private MouseHandler port_drag;
	public String title = "";
	public final String name;
	public final boolean output;
	public final UINode uinode;
	public boolean draw_default_port = true; // Set to false if subclass wants to draw its own port instead
	public Color color = AUX_PORT;

	UIPort(String name, boolean output, UINode uinode) {
		super();
		this.name = name;
		this.output = output;
		this.uinode = uinode;

		UIPort self = this;
		port_drag = new MouseHandler(this);
		port_drag.setHandler(new MouseHandler.Handler() {

			@Override
			public void onMouseOver() {
				UIPortTemporary temp_port = getUINetObject().getTemporaryPort();
				if(temp_port != null)
					temp_port.target = self; // Set us as the current receiver of the line. If user drops the line here, we will be connected
			}

			@Override
			public void onMouseOut() {
				UIPortTemporary temp_port = getUINetObject().getTemporaryPort();
				if(temp_port != null && temp_port.target == self)
					temp_port.target = null;
			}

			@Override
			public void onMouseDown(Point position) { // Creates a UITemporaryPort that we drag from
				if(!self.output) {// Input ports can only have 1 line connected
					HashSet<UIPort> connected_ports = getUINetObject().getAllConnectedPorts(self);
					if(connected_ports.size() == 1) {
						getUINetObject().disconnectAll(self); // Disconnect all ports from ourself (should only be up to 1 connected)

						// Reconnect temporary port from the port we were already connected to
						for(UIPort p : connected_ports)
							createTempPort(p, self);
					} else if(connected_ports.size() == 0) {
						createTempPort(self, null);
					} else {
						throw new RuntimeException("Multiple lines connected to an input port. Not allowed.");
					}
				} else {
					createTempPort(self, null);
				}
			}
		});
	}

	protected void onDraw() {
		if(draw_default_port) {
			draw.setColor(50, 60, 50);
			draw.fillOval(-5.5f, -5.5f, 11f, 11f);

			draw.setColor(100, 100, 100);
			draw.fillOval(-5f, -5f, 10f, 10f);

			draw.setColor(color.red, color.green, color.blue);
			draw.fillOval(-4f, -4f, 8f, 8f);

			/*draw.setColor(0, 0, 0);
			draw.setFont("SansSerif", 10f);
			draw.text(title, 10f, 5f);*/
		}

		super.onDraw();
	}

	protected void onEvent(net.merayen.elastic.ui.event.UIEvent event) {
		port_drag.handle(event);
	}

	public UINode getNode()  {
		return uinode;
	}

	protected UINet getUINetObject() {
		return uinode.getUINet();
	}

	private void createTempPort(UIPort source_port, UIPort destination_port) {
		new UIPortTemporary(uinode, source_port, destination_port);
	}
}
