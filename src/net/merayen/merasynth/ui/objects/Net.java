package net.merayen.merasynth.ui.objects;

import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;

/*
 * TODO Restore all lines from the actual netlist!
 * Do note that all the nodes must have been initialized and created their ports, first!
 * (Maybe launch our reconnect job on the first onDraw()? After onInit(), onRestore()?
 */

public class Net extends net.merayen.merasynth.ui.objects.Group {
	/* 
	 * Draws the net behind all of the nodes. Must be drawn first.
	 */
	private class Connection {
		public UIObject a, b;

		public Connection(UIObject a, UIObject b) {
			this.a = a;
			this.b = b;
		}
	}

	ArrayList<Connection> connections = new ArrayList<Connection>();

	private UIObject dragging_port;

	protected void onDraw() {
		for(Connection c : connections) {
			if(c.a.isReady() && c.b.isReady()) { // We need to see if they are ready, otherwise translation isn't available
				Point p1 = c.a.getAbsolutePosition();
				Point p2 = c.b.getAbsolutePosition();

				draw.setColor(150, 150, 150);
				draw.setStroke(0.5f);
				draw.line(p1.x, p1.y, p2.x, p2.y);

				draw.setColor(200, 200, 200);
				draw.setStroke(0.3f);
				draw.line(p1.x, p1.y, p2.x, p2.y);
			}
		}

		super.onDraw();
	}

	public void addLine(UIObject a, UIObject b) {
		for(Connection c : connections)
			if((c.a == a && c.b == b) || (c.a == b && c.b == a))
				throw new RuntimeException("Port is already connected.");

		connections.add(new Connection(a, b));
	}

	public void removeLine(UIObject a, UIObject b) {
		for(int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			if((c.a == a && c.b == b) || (c.a == b && c.b == a)) {
				connections.remove(i);
				return;
			}
		}
	}

	public void setDraggingPort(UIObject port) {
		/*
		 * Call this when a port is dragging a line from it.
		 * This port can then be retrieved by a hovering port by calling getOtherPort()
		 */
		if(dragging_port != null)
			throw new RuntimeException("Already dragging");

		dragging_port = port;
	}

	public UIObject getDraggingPort() {
		return dragging_port;
	}

	public void updateConnectionsFromNet() {
		/*
		 * Updates our lines and connections from the netnode-system.
		 * TODO assert that all the netnodes and uinodes has the same ports available,
		 * so we might need to wait for all the UINodes to be ready.
		 */
	}
}
