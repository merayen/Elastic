package net.merayen.merasynth.netlist.datapacket;

public class RequestPacket extends DataPacket {
	/*
	 * Never ending variable that is made by the right-most requesting node.
	 * This is used for synchronization.
	 */
	public long playback_sample_count;
}
