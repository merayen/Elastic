package net.merayen.elastic.ui.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.node.UIPortTemporary;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.Postmaster;

/**
 * Draws the lines between the ports on the nodes.
 */
public class UINet extends UIObject {

	/**
	 * Class keeps all the connections in the UI. Gets gradually build from the messages sent from backend. 
	 */
	private class Connection {
		public UIPort a, b;

		public Connection(UIPort a, UIPort b) {
			this.a = a;
			this.b = b;
		}

		public String toString() {
			return "Connection<a=" + a.name + ", b=" + b.name;
		}
	}

	private List<Connection> connections = new ArrayList<Connection>();

	private UIPortTemporary temporary_port; // Invisible port that is dragged
	private UIPort dragging_port_source;

	@Override
	public void onDraw(Draw draw) {
		Window window = UINodeUtil.getWindow(this);
		draw.disableOutline();

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
					window.debug.set("UINet UITemporaryPort: %s\n", p1);

				if (c.b instanceof net.merayen.elastic.ui.objects.node.UIPortTemporary)
					window.debug.set("UINet UITemporaryPort: %s\n", p2);
			}
		}

		StringBuilder sb = new StringBuilder();
		for(Connection c : connections)
			sb.append(String.format("%s <-> %s", c.a, c.b));

		UINodeUtil.getWindow(this).debug.set("UI port connections", String.format("%d: %s", connections.size(), sb));
	}

	/**
	 * Draws line between two UIObjects.
	 * Use connect() afterwards to actually connect two ports.
	 */
	public void addLine(UIPort a, UIPort b) {
		removeLine(a, b);
		connections.add(new Connection(a, b));
	}

	public void removeLine(UIObject a, UIObject b) {
		for(int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			if((c.a == a && c.b == b) || (c.a == b && c.b == a))
				connections.remove(i);
		}
	}

	public void connect(UIPort a, UIPort b) {
		UINodeUtil.getTop(this).sendMessage(new NodeConnectMessage(a.getNode().getNodeId(), a.name, b.getNode().getNodeId(), b.name));
	}

	private void internalConnect(NodeConnectMessage message) {
		NodeView nv = getNodeView();
		UINode node_a = nv.getNode(message.node_a);
		UINode node_b = nv.getNode(message.node_b);

		if(node_a == null || node_b == null) {
			System.out.printf("ERROR: Could not connect nodes together as one/both does not exist in the UI. Sync issues? (node_a=%s, node_b=%s)\n", message.node_a, message.node_b);
			return;
		}

		UIPort port_a = node_a.getPort(message.port_a);
		UIPort port_b = node_b.getPort(message.port_b);

		if(port_a == null || port_b == null) {
			System.out.printf("ERROR: Could not connect ports together as one/both ports does not exist in the UI. Sync issues? (port_a=%s, port_b=%s)\n", port_a, port_b);
			return;
		}

		connections.add(new Connection(port_a, port_b));
	}

	public void disconnect(UIPort a, UIPort b) {
		UINodeUtil.getTop(this).sendMessage(new NodeDisconnectMessage(a.getNode().getNodeId(), a.name, b.getNode().getNodeId(), b.name));
	}

	private void internalDisconnect(NodeDisconnectMessage message) {
		for(int i = connections.size() - 1; i > -1; i--) {
			Connection c = connections.get(i);
			String node_a = c.a.getNode().getNodeId();
			String node_b = c.b.getNode().getNodeId();
			if(node_a.equals(message.node_a) && node_b.equals(message.node_b)) {
				if(c.a.name.equals(message.port_a) && c.b.name.equals(message.port_b))
					connections.remove(i);
			} else if(node_a.equals(message.node_b) && node_b.equals(message.node_a)) {
				if(c.a.name.equals(message.port_b) && c.b.name.equals(message.port_a))
					connections.remove(i);
			}
		}
	}

	private void internalRemoveNode(String node_id) {
		// Remove all connections
		for(int i = connections.size() - 1; i > -1; i--) {
			Connection c = connections.get(i);
			UIPort port = null;
			if(c.a.getNode().getNodeId().equals(node_id))
				port = c.a;
			else if(c.b.getNode().getNodeId().equals(node_id))
				port = c.b;

			if(port != null)
				connections.remove(i);
		}
	}

	private void internalRemovePort(RemoveNodePortMessage message) {
		Iterator<Connection> iterator = connections.iterator();
		while(iterator.hasNext()) {
			Connection c = iterator.next();

			if(c.a.getNode().getNodeId().equals(message.node_id) && c.a.name.equals(message.port))
				iterator.remove();
			else if(c.b.getNode().getNodeId().equals(message.node_id) && c.b.name.equals(message.port))
				iterator.remove();
		}
	}

	/**
	 * Disconnects all connections on a port.
	 */
	public void disconnectAll(UIPort p) {
		Top top = UINodeUtil.getTop(this);
		for(Connection c : connections)
			if(p == c.a || p == c.b)
				top.sendMessage(new NodeDisconnectMessage(c.a.getNode().getNodeId(), c.a.name, c.b.getNode().getNodeId(), c.b.name));
	}

	public void handleMessage(Postmaster.Message message) {
		if(message instanceof NodeConnectMessage)
			internalConnect((NodeConnectMessage)message);

		else if(message instanceof NodeDisconnectMessage)
			internalDisconnect((NodeDisconnectMessage)message);

		else if(message instanceof RemoveNodeMessage)
			internalRemoveNode(((RemoveNodeMessage)message).node_id);

		else if(message instanceof RemoveNodePortMessage)
			internalRemovePort((RemoveNodePortMessage)message);
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

	public void setDraggingPort(UIPort source_port, UIPortTemporary temporary_port) {
		/*
		 * Call this when a port is dragging a line from it.
		 * This port can then be retrieved by a hovering port by calling getOtherPort()
		 */
		this.temporary_port = temporary_port;
		/*if(dragging_port_source != null)
			for(int i = connections.size() - 1; i > -1; i--)
				if(connections.get(i).a instanceof UIPortTemporary || connections.get(i).b instanceof UIPortTemporary)
					connections.remove(i);*/

		dragging_port_source = source_port;
	}

	public UIPort getDraggingSourcePort() {
		return dragging_port_source;
	}

	public UIPortTemporary getTemporaryPort() {
		return temporary_port;
	}

	/**
	 * Retrieves all the other UIPort connected to us
	 */
	public HashSet<UIPort> getAllConnectedPorts(UIPort p) {
		HashSet<UIPort> result = new HashSet<>();

		for(Connection c : connections) {
			if(p == c.a)
				result.add(c.b);
			else if(p == c.b)
				result.add(c.a);
		}

		return result;
	}

	public void reset() {
		connections.clear();
		//dragging_port = null;
		dragging_port_source = null;
	}

	private NodeView getNodeView() {
		UIObject o = this;
		while(!(o instanceof NodeView))
			o = o.getParent();

		return (NodeView)o;
	}
}
