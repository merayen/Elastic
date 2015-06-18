package net.merayen.merasynth.netlist;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Port extends NetListObject {
	private java.util.ArrayList<Line> lines = new ArrayList<Line>(); // Lines connected to us
	private Node node; // The node containing this node
	private Supervisor supervisor;
	private ArrayList<DataPacket> incoming = new ArrayList<DataPacket>();
	
	public final String name; // Our name
	
	public Port(Node node, String name) {
		super(node.supervisor);
		this.name = name;
		this.node = node;
		this.supervisor = node.supervisor;
	}
	
	public void send(DataPacket data) {
		for(Line l : lines)
			l.send(this, data);
	}
	
	public void push(DataPacket data) {
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
	
	public void connectLine(Line line) {
		/*
		 * Use supervisor's connect(), don't call this manually.
		 */
		lines.add(line);
	}
	
	public void disconnectLine(Line line) {
		assert lines.contains(line) : "Line is not connected to this port";
		lines.remove(line);
	}
	
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("id", this.getID());
		
		JSONArray lines = new JSONArray();
		
		for(Line l : this.lines)
			lines.add(l.getID());
		
		result.put("lines", lines);
		
		return result;
	}
}
