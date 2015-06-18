package net.merayen.merasynth.netlist;

import java.util.UUID;

import org.json.simple.JSONObject;

public class NetListObject {
	/*
	 * Absolutt alle i en netlist må inheritere denne.
	 */
	
	protected Supervisor supervisor;
	protected String id = new Integer(UUID.randomUUID().hashCode()).toString();
	
	public NetListObject(Supervisor supervisor) {
		this.supervisor = supervisor;
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String id) {
		/*
		 * Every object gets an ID. This exists due to restoring from JSON.
		 */
		this.id = id;
	}
	
	protected void onDump(JSONObject state) {
		
	}
	
	protected void onRestore(JSONObject state) {
		
	}
}
