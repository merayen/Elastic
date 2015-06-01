package net.merayen.merasynth.netlist;

public class NetListObject {
	/*
	 * Absolutt alle i en netlist må inheritere denne.
	 */
	private static int id_counter = 0;
	private int id;
	
	protected Supervisor supervisor;
	
	public NetListObject(Supervisor supervisor) {
		this.supervisor = supervisor;
		id = ++id_counter;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		/*
		 * Every object gets an ID. This exists due to dumping to JSON.
		 */
		this.id = id;
		if(id > id_counter)
			id_counter = id;
	}
}
