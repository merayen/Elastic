package net.merayen.merasynth.net.util.flow.portmanager;

import net.merayen.merasynth.netlist.datapacket.DataPacket;

public class ManagedPortState {
	public Class<? extends DataPacket> format; // Format on this port. Set by first sent or received packet
	public long total_bytes_transferred;
	public boolean output;
}
