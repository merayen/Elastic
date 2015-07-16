package net.merayen.merasynth.netlist;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

import net.merayen.merasynth.netlist.exceptions.AlreadyConnected;
import net.merayen.merasynth.netlist.exceptions.NoSuchConnectionException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/*
 * TODO Remove support of dump/restore?
 * It might be best to let the glue nodes restore everything.
 * Less complexity? Hmm... 
 */

public class Supervisor {
	/*
	 * Supervisor containing the whole net.
	 */
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Line> lines = new ArrayList<Line>();

	public void addNode(Node node) {
		nodes.add(node);
	}

	public void removeNode(Node node) {
		if(!nodes.contains(node))
			throw new RuntimeException(String.format("Node %s does not exist in this supervisor", node));

		// Disconnect all ports
		for(Port p : node.getPorts())
			disconnectAll(p);

		nodes.remove(node);
	}

	public void update(double timeout) {
		/*
		 * Oppdaterer hele netlisten til den er ferdigprosessert,
		 * eller via timeout.
		 */

		// TODO distribute data between nodes. Already done ??

		for(Node n : getNodesNeedingUpdate())
			n.doUpdate();
	}

	private ArrayList<Node> getNodesNeedingUpdate() {
		ArrayList<Node> result = new ArrayList<>();
		for(Node n : nodes)
			if(n.needsUpdate())
				result.add(n);

		return result;
	}

	public boolean needsUpdate() {
		return getNodesNeedingUpdate().size() > 0;
	}

	public Node getNodeByID(String id) {
		for(Node node : nodes)
			if(node.getID().equals(id))
				return node;

		throw new RuntimeException(String.format("Node by id %s was not found", id));
	}

	public void connect(Port a, Port b) {
		/*
		 * Hjelpefunksjon.
		 * Kobler til to porter sammen.
		 */
		if(a == b)
			throw new RuntimeException("Port can not be connected to itself");

		validatePort(a);
		validatePort(b);

		if(hasConnection(a, b))
			throw new AlreadyConnected();

		Line line = new Line(this, a, b);
		lines.add(line);
	}

	public void disconnect(Port a, Port b) {
		if(a == b)
			throw new RuntimeException("Invalid operation");

		validatePort(a);
		validatePort(b);

		if(!hasConnection(a, b))
			throw new NoSuchConnectionException();

		for(Line l : new ArrayList<Line>(lines))
			if((l.a == a && l.b == b) || (l.a == b || l.b == a))
				lines.remove(l);
	}

	public void disconnectAll(Port p) {
		/*
		 * Disconnects port from all other ports.
		 */
		HashSet<Port> ports = getConnectedPorts(p);
		for(Line l : new ArrayList<Line>(lines))
			if(l.a == p || l.b == p)
				lines.remove(l);
	}

	public HashSet<Port> getConnectedPorts(Port p) {
		/*
		 * Retrieves all the connected lines for a port.
		 */
		validatePort(p);

		HashSet<Port> result = new HashSet<Port>();
		for(Line l : lines) {
			if(l.a == p)
				result.add(l.b);
			if(l.b == p)
				result.add(l.a);
		}

		if(result.contains(p))
			throw new RuntimeException(
				String.format("Port %s on node %s is connected to itself. Should not happen",
						p.name, p.node.getClass().getName())
			);

		return result;
	}

	public void send(Port port, DataPacket data) {
		for(Port p : getConnectedPorts(port))
			p.supervisor_push(data);
	}

	public ArrayList<Line> getLines() {
		return new ArrayList<Line>(lines);
	}

	private boolean hasConnection(Port a, Port b) {
		// We check both ways to be extremely sure
		for(Port p : getConnectedPorts(a))
			if(p == b)
				return true;

		for(Port p : getConnectedPorts(b))
			if(p == a)
				return true;

		return false;
	}

	private void validatePort(Port p) {
		if(p == null)
			throw new RuntimeException("Port can not be null");

		if(p.supervisor != this)
			throw new RuntimeException("Port does not share the same node supervisor");

		for(Node n : nodes)
			if(n == p.node)
				return; // All fine

		throw new RuntimeException("Supervisor does not have this node");
	}

	public JSONObject dump() {
		/*
		 * Dumps the nodes and the netlist.
		 * TODO Dump the lines the port are connected to, too!
		 */
		JSONObject result = new JSONObject();

		// Dump of nodes
		JSONArray nodes = new JSONArray();
		for(Node n : this.nodes)
			nodes.add(n.dump());

		// Dump of net
		JSONArray lines = new JSONArray();
		for(Line l : this.lines)
			lines.add(l.dump());

		result.put("lines", lines);	
		result.put("nodes", nodes);

		return result;
	}

	public void restore(JSONObject obj) {
		JSONArray nodes = (JSONArray)obj.get("nodes");
		JSONArray lines = (JSONArray)obj.get("lines");

		// Creation of nodes and their ports
		for(int i = 0; i < nodes.size(); i++ ) {
			JSONObject x = (JSONObject)nodes.get(i);
			String class_name = (String)x.get("class");
			Node node;

			try {
				node = (Node)Class.forName(class_name).getConstructor(Supervisor.class).newInstance(this);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate class: " + e.toString());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not find class: " + class_name);
			}

			node.restore((JSONObject)x);

			this.nodes.add(node);
		}

		// Connecting the ports with lines
		for(int i = 0; i < lines.size(); i++ ) {
			JSONObject line = (JSONObject)lines.get(i);
			JSONObject port_a = (JSONObject)line.get("port_a");
			JSONObject port_b = (JSONObject)line.get("port_b");

			Node node_a = getNodeByID((String)port_a.get("node"));
			Node node_b = getNodeByID((String)port_b.get("node"));
			assert node_a != null;
			assert node_b != null;

			Port node_port_a = node_a.getPort((String)port_a.get("name"));
			Port node_port_b = node_b.getPort((String)port_b.get("name"));

			connect(node_port_a, node_port_b);
		}
	}
}
