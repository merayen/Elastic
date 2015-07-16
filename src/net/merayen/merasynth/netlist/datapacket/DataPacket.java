package net.merayen.merasynth.netlist.datapacket;

public abstract class DataPacket {
	/*
	 * All datapackets must inherits this class.
	 * Represents a packet that is sent between the nodes.
	 */
	private static long next_id = 0;

	public final long id = ++next_id; // Unique packet id, incremental

	public final long created = System.currentTimeMillis();
}
