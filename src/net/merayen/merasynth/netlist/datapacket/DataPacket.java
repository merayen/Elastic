package net.merayen.merasynth.netlist.datapacket;

import net.merayen.merasynth.netlist.Port;

/**
 * All datapackets must inherits this class.
 * Represents a packet that is sent between the nodes.
 * A packet *CAN NOT* be forwarded, it must be created again.
 */
public abstract class DataPacket {
	private static long next_id = 0;

	/**
	 * Gets sent to all running processors
	 */
	public static final long ALL_SESSIONS = -1;

	/**
	 * All nodes has what is called "control session". This session is not processed by the processors.
	 */
	public static final long CONTROL_SESSION = -2;

	/**
	 * Main session. All processors that or not a voice (like in a poly synth), processes by this
	 * session by default.
	 */
	public static final long MAIN_SESSION = 0; 

	public final long id = ++next_id; // Unique packet id, incremental
	public Port sender_port; // Do we really need this?

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
