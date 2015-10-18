package net.merayen.merasynth.netlist;

import java.util.ArrayList;

import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.exceptions.NoSuchPortException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public abstract class Node extends NetListObject {
	/*
	 * Alle noder må inheritere fra denne.
	 */
	public static class PortExists extends RuntimeException {
		public PortExists(Node node, String port_name) {
			super(String.format("Port '%s' already exists on node '%s'", node.getClass().getName(), port_name));
		}
	}

	// Returner denne i update() for å fortelle Supervisoren at vi ikke trenger mer oppmerksomhet
	protected final double DONE = 1000000.0;
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

	protected void onReceive(String port_name, DataPacket dp) {

	}

	protected double onUpdate() {
		return DONE;
	}

	protected void onDump(JSONObject state) {

	}

	protected void onDestroy() {
		
	}

	public void addPort(String port_name) {
		/*
		 * Noden kaller på denne for å legge til en port
		 */
		if(getPort(port_name) != null)
			throw new PortExists(this, port_name);

		ports.add(new Port(this, port_name));
	}

	public ArrayList<Port> getPorts() {
		return new ArrayList<Port>(ports);
	}

	public Port getPort(String name) {
		for(Port p : ports)
			if(name.equals(p.name))
				return p;

		return null;
	}

	protected void send(String port_name, DataPacket data) {
		/*
		 * Node kaller på denne for å sende data ut på en port
		 */
		Port port = getPort(port_name);

		if(port == null)
			throw new NoSuchPortException(this, port_name);

		supervisor.send(port, data);
	}

	public void update() {
		// Read any data available on ports
		for(Port p : new ArrayList<Port>(ports)) {
			DataPacket dp;
			while((dp = p.receive()) != null)
				onReceive(p.name, dp);
		}

		double next_return = onUpdate();
		next_update = System.currentTimeMillis() + (long)(next_return*1000);
	}

	public boolean needsUpdate() {
		return System.currentTimeMillis() >= next_update;
	}

	protected void queueUpdate() {
		next_update = 0;
	}

	public void print(String s, Object... obj) {
		System.out.printf("[Node] " + s + "\n", obj);
	}

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
