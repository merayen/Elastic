package net.merayen.merasynth.netlist;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Port extends NetListObject {
	public final Node node; // The node containing this port
	public final Supervisor supervisor;
	private ArrayList<DataPacket> incoming = new ArrayList<DataPacket>();
	
	public final String name; // Our name
	
	public Port(Node node, String name) {
		super(node.supervisor);

		if(name == null)
			throw new RuntimeException("name is null");

		if(node == null)
			throw new RuntimeException("name is null");

		this.name = name;
		this.node = node;
		this.supervisor = node.supervisor;
	}

	public void supervisor_push(DataPacket data) {
		/*
		 * Called by another port to receive data from that port
		 */
		incoming.add(data);
		node.queueUpdate(); // Fikk data, så da må noden få kallt update()
	}

	public DataPacket receive() {
		/*
		 * Hent ut en datapakke som ventes på å bli lest
		 */
		if(incoming.isEmpty())
			return null;

		return incoming.remove(0);
	}
	
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("id", this.getID());
		
		return result;
	}
}
