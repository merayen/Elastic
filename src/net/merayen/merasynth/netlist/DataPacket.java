package net.merayen.merasynth.netlist;

public class DataPacket {
	/*
	 * Datapakke. Kan inneholde hva som helst. Sendes mellom noder.
	 */
	protected Object data;
	
	public DataPacket(Object data) {
		this.data = data;
	}
	
	public Object getData() {
		return data;
	}
}
