package net.merayen.merasynth.ui.objects;

import java.util.ArrayList;
import java.util.HashSet;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.glue.nodes.GlueTop;
import net.merayen.merasynth.netlist.Line;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.Port;
import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.node.UIPort;
import net.merayen.merasynth.ui.objects.node.UINode;

/*
 * Do note that all the nodes must have been initialized and created their ports, first!
 * (Maybe launch our reconnect job on the first onDraw()? After onInit(), onRestore()?
 */

public class UINet extends net.merayen.merasynth.ui.objects.UIGroup {

	/* 
	 * Connection() class is *only* used to cache connections for fast draw times.
	 * connections-array is reloaded from the netnode system.
	 */
	private class Connection {
		public UIPort a, b;

		public Connection(UIPort a, UIPort b) {
			this.a = a;
			this.b = b;
		}
	}

	ArrayList<Connection> connections = new ArrayList<Connection>();

	private UIPort dragging_source_port;
	private UIPort dragging_temp_port;

	private boolean do_reload;

	@Override
	protected void onDraw() {
		if(do_reload)
			doReload();

		for(Connection c : connections) {
			if(c.a.isReady() && c.b.isReady()) { // We need to see if they are ready, otherwise translation isn't available
				Point p1 = getRelativePosition(c.a);
				Point p2 = getRelativePosition(c.b);

				draw.setColor(150, 150, 150);
				draw.setStroke(5f);
				draw.line(p1.x, p1.y, p2.x, p2.y);

				draw.setColor(200, 200, 200);
				draw.setStroke(3f);
				draw.line(p1.x, p1.y, p2.x, p2.y);

				if (c.a instanceof net.merayen.merasynth.ui.objects.node.UIPortTemporary)
					this.getTopObject().debug.set("UINet UITemporaryPort: %s\n", p1);

				if (c.b instanceof net.merayen.merasynth.ui.objects.node.UIPortTemporary)
					this.getTopObject().debug.set("UINet UITemporaryPort: %s\n", p2);

			}
		}

		super.onDraw();
	}

	/*
	 * Draws line between two UIObjects.
	 * Use connect() afterwards to actually connect two ports.
	 */
	public void addLine(UIPort a, UIPort b) {
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

	public void connect(UIPort a, UIPort b) {
		Port a_node_port = getNetPort(a);
		Port b_node_port = getNetPort(b);

		if(a_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", a.name, a.getNode().getClass().getName()));

		if(b_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", b.name, b.getNode().getClass().getName()));

		this.getSupervisor().connect(a_node_port, b_node_port);

		reload(); // Reload our changes to netnodes back to us
	}

	public void disconnect(UIPort a, UIPort b) {
		Port a_node_port = getNetPort(a);
		Port b_node_port = getNetPort(b);

		if(a_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", a.name, a.getNode().getClass().getName()));

		if(b_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", b.name, b.getNode().getClass().getName()));

		getSupervisor().disconnect(a_node_port, b_node_port);

		reload(); // Reload our changes to netnodes back to us
	}

	public void disconnectAll(UIPort p) {
		/*
		 * Disconnects all connections on a port.
		 */
		Port node_port = getNetPort(p);

		if(node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", p.name, p.getNode().getClass().getName()));

		getSupervisor().disconnectAll(node_port);

		reload();
	}

	public void setDraggingPort(UIPort source_port, UIPort temp_port) {
		/*
		 * Call this when a port is dragging a line from it.
		 * This port can then be retrieved by a hovering port by calling getOtherPort()
		 */
		dragging_source_port = source_port;
		dragging_temp_port = temp_port;
		addLine(source_port, temp_port);
	}

	public void removeDraggingPort() {
		dragging_source_port = null;
		dragging_temp_port = null;
		reload();
	}

	public UIPort getDraggingSourcePort() {
		return dragging_source_port;
	}

	public HashSet<UIPort> getAllConnectedPorts(UIPort p) {
		/*
		 * Retrieves all the other UIPort connected to us
		 */
		HashSet<UIPort> result = new HashSet<UIPort>();
		Port net_port = getNetPort(p);
		HashSet<Port> connected_net_ports = this.getSupervisor().getConnectedPorts(net_port);
		for(Port x : connected_net_ports) {
			GlueNode glue_node = this.getGlueTop().getNodeByNetNodeID(x.node.getID());
			UINode uinode = glue_node.getUINode();
			UIPort uiport = uinode.getPort(x.name);
			if(uiport == null)
				throw new RuntimeException(String.format("Port %s was not found on UINode %s", x.name, uinode));

			result.add(uiport);
		}

		return result;
	}

	private Port getNetPort(UIPort p) {
		UINode uinode = p.getNode();
		GlueNode glue_node = uinode.getGlueNode();
		Node node = glue_node.getNetNode();
		return node.getPort(p.name);
	}

	/*
	 * Schedules an async reload. Will happen soon, but no more often than for each drawn frame.
	 */
	public void reload() {
		do_reload = true;
	}

	private void doReload() {
		/*
		 * Updates our lines and connections from the netnode-system.
		 */
		do_reload = false;
		if(!isAlive())
			return;

		connections.clear(); // XXX Re-use existing connections? Less GC?
		GlueTop glue_top = getGlueTop();
		for(Line l : this.getSupervisor().getLines()) {
			Node a_node = l.a.node;
			Node b_node = l.b.node;
			GlueNode a_glue_node = glue_top.getNodeByNetNodeID(a_node.getID());
			GlueNode b_glue_node = glue_top.getNodeByNetNodeID(b_node.getID());
			UINode a_uinode = a_glue_node.getUINode();
			UINode b_uinode = b_glue_node.getUINode();
			UIPort a_uiport = a_uinode.getPort(l.a.name);
			UIPort b_uiport = b_uinode.getPort(l.b.name);

			if(a_uiport == null) {
				//System.out.printf("UINode %s is missing port %s\n", a_uinode.getClass().getName(), l.a.name);
				continue;
			}

			if(b_uiport == null) {
				//System.out.printf("UINode %s is missing port %s\n", b_uinode.getClass().getName(), l.b.name);
				continue;
			}

			connections.add(new Connection(a_uiport, b_uiport));
		}

		// Connect dragging port, if any
		if(dragging_source_port != null) {
			connections.add(new Connection(dragging_source_port, dragging_temp_port));
		}
	}

	private Supervisor getSupervisor() {
		/*
		 * Return the supervisor from the netnode system.
		 */
		return getTopObject().getSupervisor();
	}

	private GlueTop getGlueTop() {
		return getTopObject().getGlueTop();
	}
}
