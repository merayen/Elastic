package net.merayen.elastic.ui.objects;

import java.util.ArrayList;
import java.util.HashSet;

import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.top.Top;

/**
 * Draws the lines between the ports on the nodes.
 */
public class UINet extends UIObject {

	/* 
	 * Connection() class is *only* used to cache connections for fast draw times.
	 * TODO might make it more permanent and allow 
	 */
	private class Connection {
		public UIPort a, b;

		public Connection(UIPort a, UIPort b) {
			this.a = a;
			this.b = b;
		}
	}

	ArrayList<Connection> connections = new ArrayList<Connection>();

	private UIPort dragging_port;
	private UIPort dragging_port_source;

	//private boolean do_reload;

	@Override
	protected void onDraw() {
		Top top = (Top)search.getTop();
		//if(do_reload)
			//doReload();

		for(Connection c : connections) {
			if(c.a.isInitialized() && c.b.isInitialized()) { // We need to see if they are ready, otherwise translation isn't available
				Point p1 = getRelativePosition(c.a);
				Point p2 = getRelativePosition(c.b);

				draw.setColor(150, 150, 150);
				draw.setStroke(5f);
				draw.line(p1.x, p1.y, p2.x, p2.y);

				draw.setColor(200, 200, 200);
				draw.setStroke(3f);
				draw.line(p1.x, p1.y, p2.x, p2.y);

				if (c.a instanceof net.merayen.elastic.ui.objects.node.UIPortTemporary)
					top.debug.set("UINet UITemporaryPort: %s\n", p1);

				if (c.b instanceof net.merayen.elastic.ui.objects.node.UIPortTemporary)
					top.debug.set("UINet UITemporaryPort: %s\n", p2);

			}
		}

		super.onDraw();
	}

	/**
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
		System.out.println("Connecting is not implemented"); // TODO send a message to backend postmaster
		return;

		/*Port a_node_port = getNetPort(a);
		Port b_node_port = getNetPort(b);

		if(a_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", a.name, a.getNode().getClass().getName()));

		if(b_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", b.name, b.getNode().getClass().getName()));

		//this.getNetList().connect(a_node_port, b_node_port);

		//reload(); // Reload our changes to netnodes back to us*/
	}

	public void disconnect(UIPort a, UIPort b) {
		System.out.println("Disonnecting is not implemented"); // TODO send a message to backend postmaster

		/*Port a_node_port = getNetPort(a);
		Port b_node_port = getNetPort(b);

		if(a_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", a.name, a.getNode().getClass().getName()));

		if(b_node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", b.name, b.getNode().getClass().getName()));

		//getNetList().disconnect(a_node_port, b_node_port);

		//reload(); // Reload our changes to netnodes back to us*/
	}

	/**
	 * Disconnects all connections on a port.
	 */
	public void disconnectAll(UIPort p) {
		System.out.println("Disonnecting all is not implemented"); // TODO send a message to backend postmaster
		/*Port node_port = getNetPort(p);

		if(node_port == null)
			throw new RuntimeException(String.format("Netnode is missing port %s (UINode %s)", p.name, p.getNode().getClass().getName()));

		System.out.println("Disconnecting is not implemented"); // TODO send a message to backend postmaster
		//getSupervisor().disconnectAll(node_port);

		//reload();*/
	}

	/*
	 * See if a port is connected. Does not bother the underlying net-system (that runs in another thread),
	 * but does a local lookup in our table.
	 */
	public boolean isConnected(UIPort port) {
		for(Connection c : connections)
			if(c.a == port || c.b == port)
				return true;

		return false;
	}

	public void setDraggingPort(UIPort source_port, UIPort port) {
		/*
		 * Call this when a port is dragging a line from it.
		 * This port can then be retrieved by a hovering port by calling getOtherPort()
		 */
		dragging_port = port;
		dragging_port_source = source_port;
	}

	public UIPort getDraggingPort() {
		return dragging_port_source;
	}

	/**
	 * Retrieves all the other UIPort connected to us
	 */
	public HashSet<UIPort> getAllConnectedPorts(UIPort p) {
		throw new RuntimeException("Not implemented"); // TODO 
		/*
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

		return result;*/
	}

	/*private Port getNetPort(UIPort p) {
		UINode uinode = p.getNode();
		GlueNode glue_node = uinode.getGlueNode();
		Node node = glue_node.getNetNode();
		return node.getPort(p.name);
	}*/

	/*
	 * Schedules an async reload. Will happen soon, but no more often than for each drawn frame.
	 */
	/*public void reload() {
		do_reload = true;
	}*/

	/**
	 * Updates our lines and connections from the netnode-system.
	 */
	/*private void doReload() {
		do_reload = false;
		if(!isAlive())
			return;

		connections.clear();
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

		// Re add dragging port, if any
		if(dragging_port != null && dragging_port_source != null)
			connections.add(new Connection(dragging_port_source, dragging_port));
	}*/

	/*private NetList getSupervisor() {
		return getTopObject().getSupervisor();
	}*/

	/*private GlueTop getGlueTop() {
		return getTopObject().getGlueTop();
	}*/
}
