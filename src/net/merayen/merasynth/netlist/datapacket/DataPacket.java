package net.merayen.merasynth.netlist.datapacket;

public abstract class DataPacket {
	/*
	 * All datapackets must inherits this class.
	 * Represents a packet that is sent between the nodes.
	 */
	private static long next_id = 0;

	/*
	 * All nodes has what is called "main session". This session is always running,
	 * and is not dependable on the asynchronous (time wise) processors the nodes contain
	 */
	public static final long ALL_SESSIONS = -1;
	public static final long MAIN_SESSION = -2;

	public final long id = ++next_id; // Unique packet id, incremental

	//public final long created = System.currentTimeMillis();

	/*
	 * How many samples this packet represents.
	 */
	public int sample_count;

	/*
	 * Requesting node can ask left nodes for individual sessions.
	 * Responding node can open sessions by adding sessions.
	 * Used with voices etc, where like every voice should be handled individually,
	 * which basically creates multiple instances of one node.
	 * Format: Array of random generated numbers for each session.
	 * When a session disappears, it should be de-allocated in node, meaning the session is deleted.
	 */
	public long session_id;

	protected static final int size = 8 + 4 + 8;
	/*
	 * Estimated memory the packet takes. Only the content.
	 * Override this if you have your own properties in your class.
	 */
	public int getSize() {
		return size;
	}
}
