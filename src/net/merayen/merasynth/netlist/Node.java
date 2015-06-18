package net.merayen.merasynth.netlist;

import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public abstract class Node extends NetListObject {
	/*
	 * Alle noder må inheritere fra denne.
	 */
	
	// Returner denne i update() for å fortelle Supervisoren at vi ikke trenger mer oppmerksomhet
	protected final double DONE = 1000000.0;
	private long next_update = 0; 
	
	private ArrayList<Port> ports = new ArrayList<Port>(); // Alle porter på denne noden
	
	public Node(Supervisor supervisor) {
		super(supervisor);
	}
	
	//public abstract String getName();
	
	protected void addPort(Port port) {
		/*
		 * Noden kaller på denne for å legge til en port
		 */
		assert getPort(port.name) != null : "Port already exists";
		ports.add(port);
	}
	
	public ArrayList<Port> getPorts() {
		return (ArrayList<Port>)ports.clone();
	}
	
	public Port getPort(String name) {
		for(Port p : ports)
			if(name.equals(p.name))
				return p;
		
		return null;
	}
	
	protected void send(String port_name, DataPacket data) {
		/*
		 * Node kaller denne for å sende data ut på en port
		 */
		Port port = getPort(port_name);
		
		assert port != null : "Port by name " + port_name + " does not exist on this node";
		
		port.send(data);
	}
	
	/*
	 * Kalles av supervisoren hyppig.
	 * Her kan noden prosessere data, motta og sende.
	 * Returner antall sekunder for når noden ønsker å bli kallt på igjen.
	 * Den blir kallt omigjen uansett om den mottar data på en port.
	 */
	public abstract double update();
	
	protected abstract void freezeState(JSONObject state);
	
	public void doUpdate() {
		/*
		 * Kalles av supervisoren.
		 */
		double next_return = this.update();
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
		result.put("name", this.getClass().toString());
		
		JSONArray ports = new JSONArray();
		for(Port p : getPorts())
			ports.add(p.dump());
		
		result.put("ports", ports);
		
		JSONObject state = new JSONObject();
		freezeState(state);
		result.put("state", state);
		
		return result;
	}
}
