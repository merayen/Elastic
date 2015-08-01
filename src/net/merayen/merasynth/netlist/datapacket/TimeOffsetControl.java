package net.merayen.merasynth.netlist.datapacket;

public class TimeOffsetControl extends DataPacket {
	/*
	 * Sends time offset to sequencer and piano roll nodes.
	 * Use this to scroll to forward and backwards in time.
	 */
	public double time; // New time in seconds to set

	public int getSize() {
		return 8;
	}
}
