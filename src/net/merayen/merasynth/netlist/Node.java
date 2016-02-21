package net.merayen.merasynth.netlist;

import java.util.ArrayList;
import java.util.Set;

import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.exceptions.NoSuchPortException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public abstract class Node extends NetListObject {
	public static class PortExists extends RuntimeException {
		public PortExists(Node node, String port_name) {
			super(String.format("Port '%s' already exists on node '%s'", node.getClass().getName(), port_name));
		}
	}

	public static class PortNotFound extends RuntimeException {
		public PortNotFound(Node node, String port_name) {
			super(String.format("Port '%s' not found on node '%s'", node.getClass().getName(), port_name));
		}
	}

	// Returner denne i update() for å fortelle Supervisoren at vi ikke trenger mer oppmerksomhet
	protected final double DONE = 10000000000.0;
	private long next_update = 0; 

	private ArrayList<Port> ports = new ArrayList<Port>(); // Alle porter på denne noden

	public Node(Supervisor supervisor) {
		super(supervisor);
		onCreate();
	}

	protected void onCreate() {

	}

	protected void onRestore() {

	}

	protected void onCreatePort(String port_name) {

	}

	protected void onRemovePort(String port_name) {

	}

	protected void onReceive(String port_name, DataPacket dp) {

	}

	protected double onUpdate() {
		return DONE;
	}

	/**
	 * Called when one or more wires has been added or removed from this node.
	 */
	protected void onRewire() {

	}

	protected void onDump(JSONObject state) {

	}

	protected void onDestroy() {

	}

	/**
	 * Don't call this manually
	 */
	public void addPort(String port_name) {
		if(getPort(port_name) != null)
			throw new PortExists(this, port_name);

		ports.add(new Port(this, port_name));

		onCreatePort(port_name);
	}

	public void removePort(String port_name) {
		Port port = getPort(port_name); 
		if(port == null)
			throw new PortNotFound(this, port_name);

		ports.remove(port);

		onRemovePort(port_name);
	}

	public ArrayList<Port> getPorts() {
		return new ArrayList<Port>(ports);
	}

	public Port getPort(String port_name) {
		for(Port p : ports)
			if(port_name.equals(p.name))
				return p;

		return null;
	}

	public boolean isConnected(String port_name) {
		Port p = getPort(port_name);
		if(p == null)
			throw new PortNotFound(this, port_name);

		return supervisor.isConnected(p);
	}

	public Node[] getConnectedNodes(String port_name) {
		Port p = getPort(port_name);
		if(p == null)
			throw new PortNotFound(this, port_name);

		Set<Port> ports = supervisor.getConnectedPorts(p);
		Node[] result = new Node[ports.size()];

		int i = 0;
		for(Port port : ports)
			result[i++] = port.node;

		return result;
	}

	public int getConnectionCount(String port_name) {
		Port p = getPort(port_name);
		if(p == null)
			throw new PortNotFound(this, port_name);

		return supervisor.getConnectedPorts(p).size();
	}

	/**
	 * Send data out from one of the nodes ports.
	 */
	public void send(String port_name, DataPacket data) {
		Port port = getPort(port_name);
		if(port == null)
			throw new NoSuchPortException(this, port_name);

		if(data.sender_port != null)
			throw new RuntimeException("Resending of packets are not allowed. You will need to recreate them");

		data.sender_port = port;

		supervisor.send(port, data);
	}

	/**
	 * Send data directly to a port (not the usual distribute to all connected lines-method).
	 * Note, no checks are done to see if this port is connected at all. Be careful to always
	 * be up-to-date on connected ports if using this.
	 */
	public void send(String source_port, Port destination_port, DataPacket data) {
		Port port = getPort(source_port);
		if(port == null)
			throw new NoSuchPortException(this, source_port);

		if(data.sender_port != null)
			throw new RuntimeException("Resending of packets are not allowed. You will need to recreate them");

		data.sender_port = port;

		supervisor.sendPort(destination_port, data);
	}

	public void update() {
		// Read any data available on ports
		for(Port p : new ArrayList<Port>(ports))
			for(DataPacket dp : p.retrievePackets())
				onReceive(p.name, dp);

		double next_return = onUpdate();
		next_update = System.currentTimeMillis() + (long)(next_return*1000);
	}

	public boolean needsUpdate() {
		return System.currentTimeMillis() >= next_update;
	}

	protected void queueUpdate() {
		next_update = 0;
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();

		result.put("id", this.getID());
		result.put("class", this.getClass().getName());

		JSONArray ports = new JSONArray();
		for(Port p : getPorts())
			ports.add(p.dump());

		result.put("ports", ports);

		JSONObject state = new JSONObject();
		onDump(state);
		result.put("state", state);

		return result;
	}

	public void restore(JSONObject obj) {
		this.setID((String)obj.get("id"));

		// Create/make sure port exists
		JSONArray dump_ports = (JSONArray)obj.get("ports");
		for(int i = 0; i < dump_ports.size(); i++) {
			JSONObject dump_port = (JSONObject)dump_ports.get(i);
			String port_name = (String)dump_port.get("name");
			if(getPort(port_name) == null)
				addPort(port_name);

			Port port = getPort(port_name);
			port.setID((String)dump_port.get("id"));

			// The Supervisor() connects the port afterwards
		}

		this.onRestore((JSONObject)obj.get("state"));
	}
}
