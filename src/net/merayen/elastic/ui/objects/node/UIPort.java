package net.merayen.elastic.ui.objects.node;

import java.util.HashSet;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.ui.Color;
import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UINet;
import net.merayen.elastic.ui.util.MouseHandler;

public class UIPort extends UIObject {
	public final static Color AUDIO_PORT = new Color(150, 200, 150);
	public final static Color MIDI_PORT = new Color(200, 150, 100);
	public final static Color AUX_PORT = new Color(150, 150, 150);

	private MouseHandler port_drag;
	public String title = "";
	public final String name;
	public final boolean output;
	public boolean draw_default_port = true; // Set to false if subclass wants to draw its own port instead
	public Color color = AUX_PORT;

	private UIPortTemporary temp_port; // Used when dragging a line from this port

	UIPort(String name, boolean output) {
		super();
		this.name = name;
		this.output = output;

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
			public void onMouseUp(Point position) {
				if(temp_port != null)
					removeTempPort();
				/*// Check with the Net-UIObject to see if a line is being drawn
				UIPort source_port = getUINetObject().getDraggingSourcePort(); // Retrieving the port, the port the line is being dragged from
				UIPortTemporary temp_port = getUINetObject().getTemporaryPort(); // Retrieving the port, the port the line is being dragged from

				if(source_port == null || temp_port == null) return; // Not connecting anything

				if(!self.output) // Is input port, we do not allow multiple connections
					getUINetObject().disconnectAll(self);

				if(self.output)
					if(source_port.output)
						return; // We do not allow connecting output ports together

				dropDraggingPort(source_port);*/
			}

			@Override
			public void onMouseDrop(Point start_point, Point offset) { // Called on UIPortTemporary
				tryConnect();
				removeTempPort();
			}

			@Override
			public void onMouseDrag(Point position, Point offset) {
				moveTempPort(position);
			}

			@Override
			public void onMouseDown(Point position) { // Creates a UITemporaryPort that we drag from
				if(!self.output) {// Input ports can only have 1 line connected
					HashSet<UIPort> connected_ports = getUINetObject().getAllConnectedPorts(self);
					if(connected_ports.size() > 0) {
						getUINetObject().disconnectAll(self); // Disconnect all ports from ourself (should only be up to 1 connected)

						// Reconnect temporary port from the port we were already connected to
						for(UIPort p : connected_ports)
							createTempPort(p);
					} else if(connected_ports.size() == 0) {
						createTempPort(self);
					} else {
						throw new RuntimeException("Multiple lines connected to an input port. Not allowed.");
					}
				} else {
					createTempPort(self);
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

			draw.setColor(0, 0, 0);
			draw.setFont("SansSerif", 10f);
			draw.text(title, 10f, 5f);
		}

		/*if(port_stats != null && this.absolute_translation.scale_x < .5) {
			draw.setFont("SansSerif", 6);
			draw.setColor(255, 255, 255);
			draw.text(String.format("%d kb", (int)(port_stats.bytes_transferred / 1000)), -10, 10);
			draw.text(String.format("%d/%d", port_stats.active, port_stats.total), -10, 16);
		}*/

		super.onDraw();
	}

	protected void onEvent(net.merayen.elastic.ui.event.IEvent event) {
		port_drag.handle(event);
	}

	protected void onUpdate() {
		//if(task_remove_port > 0 && --task_remove_port == 0)
		//	removeTempPort();
		/*if(task_remove_port > 0) {
			removeTempPort();
			task_remove_port = 0;
		}*/
	}

	public UINode getNode()  {
		/*
		 * Gets the UI-node that contains this port.
		 */
		UIObject x = this;
		while((x = x.getParent()) != null && !(x instanceof UINode));

		if(!(x instanceof UINode))
			throw new RuntimeException("Port is not attached to a node or is not attached at all");

		return (UINode)x;
	}

	protected UINet getUINetObject() {
		return getNode().getUINet();
	}

	/*public void setPortStats(PortStats ps) {
		port_stats = ps;
	}*/

	private void createTempPort(UIPort p) {
		if(temp_port != null)
			removeTempPort();

		temp_port = new UIPortTemporary();
		add(temp_port);
		temp_port.addTempPort(p);
		temp_port.target = this;
	}

	private void moveTempPort(Point position) { // Relative coordinates
		temp_port.translation.x = position.x;
		temp_port.translation.y = position.y;
	}

	private void removeTempPort() {
		temp_port.removeTempPort();
		remove(temp_port);
		temp_port = null;
	}

	private void tryConnect() {
		UIPort source_port = getUINetObject().getDraggingSourcePort();
		UIPortTemporary temp_port = getUINetObject().getTemporaryPort();
		UIPort target_port = temp_port.target;

		if(target_port != null) {
			if(target_port == source_port) return;
			if(target_port.getParent() == source_port.getParent()) return;

			// If we are an input port, clear any line that is already connected to us (should only be 1 line)
			try {
				getUINetObject().connect(source_port, target_port);
			} catch (NetList.AlreadyConnected e) {
				// Okido
			}
		}
	}
}
