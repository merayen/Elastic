package net.merayen.merasynth.ui.objects;

import java.util.ArrayList;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.Port;
import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.node.UIPort;
import net.merayen.merasynth.ui.objects.node.UINode;

/*
 * TODO Restore all lines from the actual netlist!
 * Do note that all the nodes must have been initialized and created their ports, first!
 * (Maybe launch our reconnect job on the first onDraw()? After onInit(), onRestore()?
 */

public class UINet extends net.merayen.merasynth.ui.objects.UIGroup {
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

	/*
	 * Draws line between two UIObjects.
	 * Use connect() to actually connect two ports.
	 */
	public void addLine(UIObject a, UIObject b) {
		for(Connection c : connections)
			if((c.a == a && c.b == b) || (c.a == b && c.b == a))
				throw new RuntimeException("Port is already connected.");

		connections.add(new Connection(a, b));
		
		//getSupervisor().connect(, b);
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

	public void connect(UIPort a, UIPort b) {
		// TODO Communicate back to the net-node net-system and then reload from it
		// TODO reload us from the net-node list
		UINode a_uinode = a.getNode();
		UINode b_uinode = b.getNode();
		GlueNode a_glue = a_uinode.getGlueNode();
		GlueNode b_glue = b_uinode.getGlueNode();
		Node a_node = a_glue.getNetNode();
		Node b_node = b_glue.getNetNode();
		Port a_node_port = a_node.getPort(a.name);
		Port b_node_port = b_node.getPort(b.name);

		if(a_node_port == null)
			throw new RuntimeException(String.format("Port was not found: %s on node %s", a.name, a_node));

		if(b_node_port == null)
			throw new RuntimeException(String.format("Port was not found: %s on node %s", b.name, b_node));

		this.getSupervisor().connect(a_node_port, b_node_port);
		System.out.printf("Hurra");
	}

	public void disconnect(UIPort a, UIPort b) {
		// TODO Communicate with net-node
		// TODO reload us
	}

	public void setDraggingPort(UIObject port) {
		/*
		 * Call this when a port is dragging a line from it.
		 * This port can then be retrieved by a hovering port by calling getOtherPort()
		 */
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

	private Supervisor getSupervisor() {
		/*
		 * Return the supervisor from the netnode system.
		 */
		return getTopObject().getSupervisor();
	}
}
