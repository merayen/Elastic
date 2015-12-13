package net.merayen.merasynth.netlist.datapacket;

public abstract class RequestPacket extends DataPacket {
	/*
	 * Never ending variable that is made by the right-most requesting node.
	 * This is used for synchronization.
	 * XXX Delete? DO we really need this?
	 */
	// public long playback_sample_count;
}
