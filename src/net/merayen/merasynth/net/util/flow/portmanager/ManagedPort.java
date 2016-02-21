package net.merayen.merasynth.net.util.flow.portmanager;

import net.merayen.merasynth.netlist.datapacket.ControlRequest;
import net.merayen.merasynth.netlist.datapacket.ControlResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.DataRequest;
import net.merayen.merasynth.netlist.datapacket.RequestPacket;
import net.merayen.merasynth.netlist.datapacket.ResponsePacket;

/**
 * Do not store an instance of this class, as it might disappears whenever.
 * ALways retrieve it by PortManager().get()
 */
public class ManagedPort {
	PortManager port_manager;
	public final String port_name;
	public ManagedPortState state;
	int connection_count; // How many ports are connected to this port

	ManagedPort(PortManager port_manager, String port_name) {
		this.port_manager = port_manager;
		this.port_name = port_name;
	}

	public void reset() {
		state = null;
	}

	/**
	 * Detects, remembers, and asserts that the same format is always being sent/received.
	 */
	private void confirmFormat(DataPacket rp) {
		if(rp instanceof ControlResponse || rp instanceof ControlRequest || rp instanceof DataRequest)
			return; // We can not figure out format from control packets. We wait for some packet that do actually have a format

		if(state.format == null)
			state.format = rp.getClass();
		else if(!rp.getClass().isAssignableFrom(state.format))
			throw new RuntimeException(String.format(
				"Unexpected format sent/received after format has been decided for port '%s': Expected %s but got %s",
				port_name,
				state.format.getName(),
				rp.getClass().getName()
			));
	}

	public void confirmSend(DataPacket rp) {
		if(state == null) {
			state = new ManagedPortState();
			state.output = rp instanceof ResponsePacket;
		}

		confirmFormat(rp);
		stats(rp);
	}

	public void confirmReceive(DataPacket rp) {
		if(state == null) {
			state = new ManagedPortState();
			state.output = rp instanceof RequestPacket;
		}

		confirmFormat(rp);
		stats(rp);
	}

	/**
	 * Updates the statistics.
	 */
	private void stats(DataPacket rp) {
		state.total_bytes_transferred += rp.getSize();
	}

	/**
	 * Retrieve the count of the lines connected to this port.
	 */
	public int getConnectionCount() {
		return connection_count;
	}
}
