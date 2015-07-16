package net.merayen.merasynth.ui.objects.node;

import java.util.ArrayList;

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

	private PortDrag temp_port; // Used when dragging a line from this port

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
				UIPort port = getNetObject().getDraggingPort(); // Retrieving the port
				if(port == null) return; // Not dragging a line from a port
				dropDraggingPort(port);
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
				if(!self.output) // Input ports can only have 1 line connected
					getNetObject().disconnectAll(self); // Disconnect all ports from ourself (should only be upto 1 connected)
				createTempPort();
			}
		});
	}

	protected void onDraw() {
		if(draw_default_port) {
			draw.setColor(50, 60, 50);
			draw.fillOval(-0.55f, -0.55f, 1.1f, 1.1f);
	
			draw.setColor(100, 100, 100);
			draw.fillOval(-0.5f, -0.5f, 1f, 1f);
	
			draw.setColor(150, 200, 150);
			draw.fillOval(-0.4f, -0.4f, 0.8f, 0.8f);
	
			draw.setColor(0, 0, 0);
			draw.setFont("SansSerif", 1.0f);
			draw.text(title, 1f, 0.5f);
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

	private UINet getNetObject() {
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

	private void createTempPort() {
		temp_port = new PortDrag();
		add(temp_port);
		getNetObject().addLine(this, temp_port);
		getNetObject().setDraggingPort(this);
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
				self.getNetObject().removeLine(self, temp_port);
				self.getNetObject().setDraggingPort(null);
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
