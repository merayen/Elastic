package net.merayen.merasynth.netlist;

import org.json.simple.JSONObject;

import net.merayen.merasynth.netlist.Port;

public class Line extends NetListObject {
	/*
	 * A connection between two ports
	 */
	public final Port a;
	public final Port b;

	public Line(Supervisor supervisor, Port a, Port b) {
		super(supervisor);
		this.a = a;
		this.b = b;
	}

	public void send(Port source, DataPacket data) {
		if(source == a)
			b.push(data);
		else if(source == b)
			a.push(data);
		else
			throw new RuntimeException("This line is not connected to this port");
	}

	public JSONObject dump() {
		JSONObject obj = new JSONObject();

		JSONObject port_a = new JSONObject();
		port_a.put("node", a.node.getID());
		port_a.put("name", a.name);
		obj.put("port_a", port_a);

		JSONObject port_b = new JSONObject();
		port_b.put("node", b.node.getID());
		port_b.put("name", b.name);
		obj.put("port_b", port_b);
		
		return obj;
	}
}
