package net.merayen.merasynth.netlist.datapacket;

/**
 * Tells a left node that it must destroy its session.
 * This typically happens if a node to the right gets a line disconnected so that it won't process anymore.
 * We release resources by doing this.
 * A new AllowNewSessionsRequest() must be sent by the rightmost node to start processing again.
 */
public class KillAllSessionsRequest extends ControlRequest {
	public KillAllSessionsRequest() {
		session_id = DataPacket.ALL_SESSIONS;
	}
}
