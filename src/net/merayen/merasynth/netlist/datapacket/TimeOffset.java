package net.merayen.merasynth.netlist.datapacket;

public class TimeOffset extends DataPacket {
	/*
	 * Piano roll, sequencer is the only one to respond to this.
	 * Use this to scroll forward and backwards in time.
	 */
	public float time; // New time in seconds to set
}
