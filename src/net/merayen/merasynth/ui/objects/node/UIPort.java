package net.merayen.merasynth.ui.objects.node;

import java.util.ArrayList;
import java.util.HashSet;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.UINet;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.util.MouseHandler;
import net.merayen.merasynth.ui.util.Search;

public class UIPort extends UIGroup {
	/*
	 * Connectable port
	 */
	public static abstract class Handler { // Only to be used by ui Node()!
		public boolean onConnect(UIPort connecting_port) {return true;} // Port has been connected
		public void onDisconnect() {} // A line has been removed from the port
	}
	private Handler handler;

	private MouseHandler port_drag;
	public String title = "";
	public final String name;
	public final boolean output;
	public boolean draw_default_port = true; // Set to false if subclass wants to draw its own port instead

	private UIPortTemporary temp_port; // Used when dragging a line from this port

	public UIPort(String name, boolean output) {
		super();
		this.name = name;
		this.output = output;
	}

	protected void onInit() {
		UIPort self = this;
		port_drag = new MouseHandler(this);
		port_drag.setHandler(new MouseHandler.Handler() {

			@Override
			public void onMouseUp(Point position) {
				// Check with the Net-UIObject to see if a line is being drawn
				UIPort source_port = getNetObject().getDraggingPort(); // Retrieving the port, the port the line is being dragged from
				if(source_port == null) return; // Not dragging a line from a port
				if(!self.output) // Is input port, we do not allow multiple connections
					getNetObject().disconnectAll(self);

				if(self.output)
					if(source_port.output)
						return; // We do not allow connecting output ports together

				dropDraggingPort(source_port);
			}

			@Override
			public void onMouseDrop(Point start_point, Point offset) {
				removeTempPort();
			}

			@Override
			public void onMouseDrag(Point position, Point offset) {
				moveTempPort(position);
			}

			@Override
			public void onMouseDown(Point position) {
				// Create a new port and notifies the net
				if(!self.output) {// Input ports can only have 1 line connected
					HashSet<UIPort> connected_ports = getNetObject().getAllConnectedPorts(self);
					if(connected_ports.size() == 1) {
						getNetObject().disconnectAll(self); // Disconnect all ports from ourself (should only be upto 1 connected)

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
	
			draw.setColor(150, 200, 150);
			draw.fillOval(-4f, -4f, 8f, 8f);
	
			draw.setColor(0, 0, 0);
			draw.setFont("SansSerif", 10f);
			draw.text(title, 10f, 5f);
		}

		super.onDraw();
	}

	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		port_drag.handle(event);
	}

	public void node_setHandler(Handler handler) {
		/*
		 * Only to be set by Node()
		 */
		this.handler = handler;
	}

	public UINode getNode()  {
		/*
		 * Gets the UI-node that contains this port.
		 */
		UIGroup x = this;
		while((x = x.parent) != null && !(x instanceof UINode));

		if(!(x instanceof UINode))
			throw new RuntimeException("Port is not attached to a node or is not attached at all");

		return (UINode)x;
	}

	protected UINet getNetObject() {
		/*
		 * Gets the Net object that draws all the lines.
		 * TODO Remove and just mangle directly with netnode lines?
		 */
		Search s = new Search(search.getTopmost(), 1);
		ArrayList<UIObject> m = s.searchByType(net.merayen.merasynth.ui.objects.UINet.class);
		if(m.size() != 1)
			throw new RuntimeException("Need exactly 1 net uiobject");

		return (UINet)m.get(0);
		// ....
	}

	private void createTempPort(UIPort p) {
		temp_port = new UIPortTemporary();
		add(temp_port);
		temp_port.addTempPort(p);
	}

	private void moveTempPort(Point position) { // Relative coordinates
		temp_port.translation.x = position.x;
		temp_port.translation.y = position.y;
	}

	private void removeTempPort() {
		UIPort self = this;
		this.draw_context.queueEvent(new DelayEvent(new Runnable() {

			@Override
			public void run() {
				self.remove(temp_port);
				temp_port.removeTempPort();
				temp_port = null;
			}
		}));
	}

	private void dropDraggingPort(UIPort port) {
		if(port == (UIPort)temp_port) return;
		if(port == this) return;
		if(port.parent == this.parent) return;

		boolean ok = true;
		if(handler != null)
			ok = handler.onConnect((UIPort)port);

		if(!ok)
			return;

		// If we are an input port, clear any line that is already connected to us (should only every be 1 line)
		//if(this.getNetObject().get)
		try {
			getNetObject().connect(this, (UIPort)port);
		} catch (net.merayen.merasynth.netlist.exceptions.AlreadyConnected e) {
			// Okido
		}
	}
}
