package net.merayen.merasynth.netlist;

import java.util.ArrayList;
import java.util.List;

import net.merayen.merasynth.netlist.datapacket.DataPacket;

import org.json.simple.JSONObject;

public class Port extends NetListObject {
	public final Node node; // The node containing this port
	public final Supervisor supervisor;
	private final List<DataPacket> incoming = new ArrayList<DataPacket>();

	public final String name; // Our name

	public Port(Node node, String name) {
		super(node.supervisor);

		if(name == null)
			throw new RuntimeException("name is null");

		this.name = name;
		this.node = node;
		this.supervisor = node.supervisor;
	}

	public void supervisor_push(DataPacket data) {
		incoming.add(data);
		node.queueUpdate(); // Fikk data, så da må noden få kallt update()
	}

	public List<DataPacket> retrievePackets() {
		/*
		 * Hent ut en datapakke som ventes på å bli lest
		 */
		List<DataPacket> result = new ArrayList<DataPacket>(incoming);
		incoming.clear();
		return result;
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result= new JSONObject();
		result.put("name", name);
		result.put("id", this.getID());

		return result;
	}
}
