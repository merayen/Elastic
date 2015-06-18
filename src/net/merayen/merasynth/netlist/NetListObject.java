package net.merayen.merasynth.netlist;

import java.util.UUID;

public class NetListObject {
	/*
	 * Absolutt alle i en netlist må inheritere denne.
	 */
	
	protected Supervisor supervisor;
	protected int id = UUID.randomUUID().hashCode();
	
	public NetListObject(Supervisor supervisor) {
		this.supervisor = supervisor;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		/*
		 * Every object gets an ID. This exists due to restoring from JSON.
		 */
		this.id = id;
	}
}
